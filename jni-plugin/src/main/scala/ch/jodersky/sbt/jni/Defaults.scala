package ch.jodersky.sbt.jni

import sbt._
import sbt.Keys._
import Keys._

import build.CMake
import build.BuildTool

import ch.jodersky.jni.Platform
import ch.jodersky.jni.NativeLoader

object Defaults {

  lazy val buildSettings: Seq[Setting[_]] = Seq(

    baseDirectory in jni := (baseDirectory in ThisBuild).value / "native",

    target in jni := target.value / "native" / (jniPlatform in jni).value.id,

    jniPlatform in jni := Platform.current.getOrElse{
      sLog.value.warn("Warning: cannot determine platform! It will be set to 'unknown'.")
      Platform.Unknown
    },

    jniBuildTool in jni := {
      val tools = Seq(CMake)

      val base = (baseDirectory in jni).value

      val tool = if (base.exists && base.isDirectory) {
        tools.find(t => t detect base)
      } else {
        None
      }
      tool.getOrElse(
        sys.error("No supported native build tool detected. " +
          s"Check that the setting 'baseDirectory in jni' (currently $base) " +
          "points to a valid directory. Supported build tools are: " +
          tools.map(_.name).mkString(",")
        )
      )
    },

    clean := {
      val log = streams.value.log
      val tool = try {
        Some((jniBuildTool in jni).value)
      } catch {
        case _: Exception => None
      }
      tool foreach { t =>
        log.debug("Cleaning native build")
        t.api.clean(
          (baseDirectory in jni).value,
          log
        )
      }
      clean.value
    },

    jni := {
      val tool = (jniBuildTool in jni).value
      val dir = (baseDirectory in jni).value
      val targetDir = (target in jni).value / "lib"
      val log = streams.value.log

      tool.api.library(dir, targetDir, log)
    },

    javahClasses in javah := Seq(),

    javahObjects in javah := Seq(),

    target in javah := (baseDirectory in jni).value,

    fullClasspath in javah := (fullClasspath in Compile).value,

    javah := {
      val out = (target in javah).value
      val jcp: Seq[File] = (fullClasspath in javah).value.map(_.data)
      val cp = jcp.mkString(sys.props("path.separator"))

      val classes = (javahClasses in javah).value ++
        (javahObjects in javah).value.map(_ + "$")

      for (clazz <- classes) {
        val parts = Seq(
          "javah",
          "-d", out.getAbsolutePath,
          "-classpath", cp,
          clazz)
        val cmd = parts.mkString(" ")
        val ev = Process(cmd) ! streams.value.log
        if (ev != 0) sys.error(s"Error occured running javah. Exit code: ${ev}")
      }

      out
    }

  )

  lazy val bundleSettings: Seq[Setting[_]] = Seq(

    jniLibraryPath in jni := {
      "/" + (organization.value + "." + name.value).replaceAll("\\.|-", "/")
    },

    unmanagedResourceDirectories in jni := Seq(
      (baseDirectory).value / "lib_native"
    ),

    unmanagedClasspath ++= (unmanagedResourceDirectories in jni).value.map{ file =>
      Attributed.blank(file)
    },

    resourceManaged in jni := (target in jni).value / "lib_managed",

    managedResourceDirectories in jni := Seq(
      (resourceManaged in jni).value
    ),

    managedClasspath ++= {

      //build native library
      val library = jni.value

      //native library as a managed resource file
      val libraryResource = (resourceManaged in jni).value /
        NativeLoader.fullLibraryPath(
          (jniLibraryPath in jni).value,
          (jniPlatform in jni).value
        )

      //copy native library to a managed resource (so it can also be loaded when not packages as a jar)
      IO.copyFile(library, libraryResource)

      Seq(Attributed.blank((resourceManaged in jni).value))
    },

    packageJni in Global := {

      val unmanagedMappings: Seq[(File, String)] = (unmanagedResourceDirectories in jni).value flatMap { dir =>
        val files = (dir **  "*").filter(_.isFile).get
        files map { file =>
          println(file.getAbsolutePath)
          file -> (file relativeTo dir).get.getPath
        }
      }

      managedClasspath.value //call this to generate files in resourceManaged

      val managedMappings: Seq[(File, String)] = (managedResourceDirectories in jni).value flatMap { dir =>
        val files = (dir **  "*").filter(_.isFile).get
        files map { file =>
          println(file.getAbsolutePath)
          file -> (file relativeTo dir).get.getPath
        }
      }

      val out = target.value / (name.value + "-native.jar")

      val manifest = new java.util.jar.Manifest
      Package.makeJar(
        unmanagedMappings ++ managedMappings,
        out,
        manifest,
        streams.value.log
      )
      out
    }

  )

  lazy val clientSettings: Seq[Setting[_]] = Seq(
    libraryDependencies += "ch.jodersky" %% "jni-library" % "0.1-SNAPSHOT",
    fork in run := true,
    artifact in jni := {
      Artifact(
        name = name.value,
        `type` = "jar",
        extension = "jar",
        classifier = Some("native"),
        configurations = Seq(Runtime),
        url = None
      ) extra (
        "platform" -> "all"
      )
    }
  ) ++ addArtifact(artifact in jni, packageJni)

}
