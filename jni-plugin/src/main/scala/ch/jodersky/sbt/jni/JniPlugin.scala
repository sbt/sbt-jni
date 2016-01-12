package ch.jodersky.sbt.jni

import build._
import ch.jodersky.jni.{NativeLoader, Platform}
import sbt._
import sbt.Keys._

object Jni extends AutoPlugin {

  override def requires = plugins.JvmPlugin

  object autoImport {

    //Main task, inspect this first
    val nativeCompile = taskKey[File]("Builds a native library (by calling the native build tool).")

    val nativePlatform = settingKey[Platform]("Platform of the system this build is running on.")

    val nativeBuildTools = taskKey[Seq[BuildTool]](
      "A collection of build tools that are tested to determine the current build environment")
    val nativeBuildTool = taskKey[BuildTool](
      "The build tool to be used when building a native library.")


  }
  import autoImport._

  lazy val mainSettings: Seq[Setting[_]] = Seq(

    nativePlatform := Platform.current.getOrElse{
      sLog.value.warn("Warning: cannot determine platform! It will be set to 'unknown'.")
      Platform.Unknown
    },

    sourceDirectory in nativeCompile := sourceDirectory.value / "native",

    target in nativeCompile := target.value / "native" / (nativePlatform).value.id,

    nativeBuildTools := Seq(CMake, Autotools),

    nativeBuildTool := {
      val tools = nativeBuildTools.value

      val src = (sourceDirectory in nativeCompile).value

      val tool = if (src.exists && src.isDirectory) {
        tools.find(t => t detect src)
      } else {
        None
      }
      tool.getOrElse(
        sys.error("No supported native build tool detected. " +
          s"Check that the setting 'sourceDirectory in nativeCompile' (currently $src) " +
          "points to a valid directory. Supported build tools are: " +
          tools.map(_.name).mkString(","))
      )

    },

    clean in nativeCompile := {
      val log = streams.value.log

      log.debug("Cleaning native build")
      try {
        val tool = nativeBuildTool.value
        tool.api.clean(
          (sourceDirectory in nativeCompile).value,
          log
        )
      } catch {
        case ex: Exception =>
          log.debug(s"Native cleaning failed: $ex")
      }

    },

    nativeCompile := {
      val tool = nativeBuildTool.value
      val src = (sourceDirectory in nativeCompile).value
      val buildDir = (target in nativeCompile).value / "build"
      val targetDir = (target in nativeCompile).value / "lib"
      val log = streams.value.log

      IO.createDirectory(buildDir)
      IO.createDirectory(targetDir)

      tool.api.library(src, buildDir, targetDir, log)
    },

    // change clean task to also clean native sources
    clean := {
      (clean in nativeCompile).value
      clean.value
    }

  )

  override lazy val projectSettings = inConfig(Compile)(mainSettings)
}
