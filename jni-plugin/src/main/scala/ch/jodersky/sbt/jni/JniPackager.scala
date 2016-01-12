package ch.jodersky.sbt.jni

import build._
import ch.jodersky.jni.{NativeLoader, Platform}
import sbt._
import sbt.Keys._

object JniPackager extends AutoPlugin {

  override def requires = Jni
  override def trigger = allRequirements

  object autoImport {

    //Main task, inspect this first
    val packageNative = taskKey[File]("Packages native libraries in a jar.")

    val nativeLibraryPath = settingKey[String](
      "String that is prepended to the path of a native library when packaged.")

    val enableNativeCompilation = settingKey[Boolean](
      "Determines if native compilation is enabled in a scoped key (typically for packaging).")

    val unmanagedNativeDirectories = settingKey[Seq[File]](
      """|Unmanaged directories containing native libraries. The libraries must be regular files
         |contained in a subdirectory corresponding to a platform.""".stripMargin)

    val unmanagedNativeLibraries = taskKey[Map[Platform, File]]("")

  }
  import autoImport._
  import Jni.autoImport._

  lazy val settings: Seq[Setting[_]] = Seq(

    nativeLibraryPath := {
      val orgPath = organization.value.replaceAll("\\.", "/")
      s"/${orgPath}/${name.value}/native"
    },

    //Make NativeLoader available to project
    libraryDependencies += "ch.jodersky" %% "jni-library" % Version.PluginVersion,

    name in packageNative := {
      name.value + "-native-" + version.value + ".jar"
    },

    enableNativeCompilation in packageNative := true,

    unmanagedNativeDirectories := Seq(baseDirectory.value / "lib_native"),

    unmanagedNativeLibraries := {
      val dirs: Seq[File] = unmanagedNativeDirectories.value
      val seq: Seq[(Platform, File)] = for (
        dir <- dirs;
        platformDir <- dir.listFiles();
        if platformDir.isDirectory;
        platform = Platform.fromId(platformDir.name);
        library <- platformDir.listFiles();
        if library.isFile
      ) yield {
        platform -> library.getCanonicalFile()
      }

      seq.toMap
    },

    packageNative := {
      val log = streams.value.log

      val unmanagedMappings: Seq[(File, String)] = {
        unmanagedNativeLibraries.value.toSeq.map{ case (platform, file) =>
          file -> NativeLoader.fullLibraryPath(
            (nativeLibraryPath in Compile).value,
            platform)
        }
      }

      val enableManaged = (enableNativeCompilation in packageNative).value

      val managedMappings: Seq[(File, String)] = if (enableManaged) {
        val library: File = (nativeCompile in Compile).value
        val path: String = NativeLoader.fullLibraryPath(
          (nativeLibraryPath in Compile).value,
          (nativePlatform in Compile).value
        )
        Seq(library -> path)
      } else {
        Seq()
      }

      val mappings = unmanagedMappings ++ managedMappings
      val manifest = new java.util.jar.Manifest
      val jar: File = (target in nativeCompile).value / (name in packageNative).value

      Package.makeJar(mappings, jar, manifest, log)
      jar
    },

    //Add native jar to runtime classpath. Note that it is not added as a resource (this would cause
    //the native library to included in the main jar).
    unmanagedClasspath in Runtime += Attributed.blank(packageNative.value),

    fork in run := true //fork new JVM, since native libraries can only be loaded once

  )
  override lazy val projectSettings = (settings)


}
