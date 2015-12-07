package ch.jodersky.sbt.jni

import build._
import ch.jodersky.jni.{NativeLoader, Platform}
import sbt._
import sbt.Keys._

object JniNative extends AutoPlugin {

  override def requires = plugins.JvmPlugin

  object autoImport {

    val jni = taskKey[File]("Builds a native library (by calling the native build tool).")

    val jniPlatform = settingKey[Platform]("Platform of the system this build is running on.")
    val jniBuildTool = taskKey[BuildTool]("The build tool to be used when building a native library.")

    val jniLibraryPath = settingKey[String]("String that is prepended to the path of a native library when packaged.")

  }
  import autoImport._

  lazy val rawSettings: Seq[Setting[_]] = Seq(

    sourceDirectory in jni := baseDirectory.value / "src",

    target in Global in jni := target.value / "native" / (jniPlatform in jni).value.id,

    jniPlatform in Global in jni := Platform.current.getOrElse {
      sLog.value.warn("Warning: cannot determine platform! It will be set to 'unknown'.")
      Platform.Unknown
    },

    jniBuildTool in jni := {
      val tools = Seq(CMake)

      val base = (sourceDirectory in jni).value

      val tool = if (base.exists && base.isDirectory) {
        tools.find(t => t detect base)
      } else {
        None
      }
      tool.getOrElse(
        sys.error("No supported native build tool detected. " +
          s"Check that the setting 'sourceDirectory in jni' (currently $base) " +
          "points to a valid directory. Supported build tools are: " +
          tools.map(_.name).mkString(","))
      )
    },

    clean in jni := {
      val log = streams.value.log
      val tool = try {
        Some((jniBuildTool in jni).value)
      } catch {
        case _: Exception => None
      }
      tool foreach { t =>
        log.debug("Cleaning native build")
        t.api.clean(
          (sourceDirectory in jni).value,
          log
        )
      }
    },

    jni := {
      val tool = (jniBuildTool in jni).value
      val dir = (sourceDirectory in jni).value
      val buildDir = (target in jni).value / "build"
      val targetDir = (target in jni).value / "lib"
      val log = streams.value.log

      IO.createDirectory(buildDir)
      IO.createDirectory(targetDir)

      tool.api.library(dir, buildDir, targetDir, log)
    },

    clean := {
      (clean in jni).value
      clean.value
    },

    jniLibraryPath in jni := {
      "/" + organization.value.replaceAll("\\.", "/") + "/" + name.value
    },

    unmanagedResourceDirectories += baseDirectory.value / "lib_native",

    resourceGenerators += Def.task {
      //build native library
      val library = jni.value

      //native library as a managed resource file
      val libraryResource = (resourceManaged in Compile).value /
        NativeLoader.fullLibraryPath(
          (jniLibraryPath in jni).value,
          (jniPlatform in jni).value
        )

      //copy native library to a managed resource (so it can also be loaded when not packaged as a jar)
      IO.copyFile(library, libraryResource)

      Seq(libraryResource)
    }.taskValue

  )

  override lazy val projectSettings = inConfig(Compile)(rawSettings)

}
