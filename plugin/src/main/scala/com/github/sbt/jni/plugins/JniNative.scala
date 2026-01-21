package com.github.sbt.jni
package plugins

import build._
import sbt._
import sbt.Keys._
import sys.process._

/**
 * Wraps a native build system in sbt tasks.
 */
object JniNative extends AutoPlugin {

  object autoImport {

    // Main task, inspect this first
    val nativeCompile = taskKey[Seq[File]](
      "Builds a native library by calling the native build tool."
    )

    val nativePlatform = settingKey[String](
      "Platform (architecture-kernel) of the system this build is running on."
    )

    val nativeBuildTool = taskKey[BuildTool](
      "The build tool to be used when building a native library."
    )

    val nativeInit = inputKey[Seq[File]](
      "Initialize a native build script from a template."
    )

    val nativeMultipleOutputs = taskKey[Boolean](
      "Enable multiple native outputs support. Disabled by default."
    )

  }
  import autoImport._

  val nativeBuildToolInstance = taskKey[BuildTool#Instance]("Get an instance of the current native build tool.")

  lazy val settings: Seq[Setting[?]] = Seq(
    // the value retruned must match that of `com.github.sbt.jni.PlatformMacros#current()` of project `macros`
    nativePlatform := {
      try {
        val (kernel, arch) = determinePlatform()
        arch + "-" + kernel
      } catch {
        case _: Exception =>
          sLog.value.error("Error trying to determine platform.")
          sLog.value.warn("Cannot determine platform! It will be set to 'unknown'.")
          "unknown-unknown"
      }
    },
    nativeCompile / sourceDirectory := sourceDirectory.value / "native",
    nativeCompile / target := target.value / "native" / nativePlatform.value,
    nativeBuildTool := {
      val tools = BuildTool.buildTools.values.toList

      val src = (nativeCompile / sourceDirectory).value

      val tool = if (src.exists && src.isDirectory) {
        tools.find(t => t.detect(src))
      } else {
        None
      }
      tool.getOrElse(
        sys.error(
          "No supported native build tool detected. " +
            s"Check that the setting 'nativeCompile / sourceDirectory' (currently set to $src) " +
            "points to a directory containing a supported build script. Supported build tools are: " +
            tools.map(_.name).mkString(",")
        )
      )
    },
    nativeMultipleOutputs := false,
    nativeBuildToolInstance := {
      val tool = nativeBuildTool.value
      val srcDir = (nativeCompile / sourceDirectory).value
      val buildDir = (nativeCompile / target).value / "build"
      IO.createDirectory(buildDir)
      tool.getInstance(
        baseDirectory = srcDir,
        buildDirectory = buildDir,
        logger = streams.value.log,
        multipleOutputs = nativeMultipleOutputs.value
      )
    },
    nativeCompile / clean := {
      val log = streams.value.log

      log.debug("Cleaning native build")
      try {
        val toolInstance = nativeBuildToolInstance.value
        toolInstance.clean()
      } catch {
        case ex: Exception =>
          log.debug(s"Native cleaning failed: $ex")
      }

    },
    nativeCompile := {
      val tool = nativeBuildTool.value
      val toolInstance = nativeBuildToolInstance.value
      val targetDir = (nativeCompile / target).value / "bin"
      val log = streams.value.log

      IO.createDirectory(targetDir)

      log.info(s"Building library with native build tool ${tool.name}")
      val libs = toolInstance.library(targetDir)
      log.success(s"Libraries built in ${libs.map(_.getAbsolutePath).mkString(", ")}")
      libs
    },

    // also clean native sources
    clean := {
      clean.dependsOn(nativeCompile / clean).value
    },
    nativeInit := {
      import complete.DefaultParsers._

      val log = streams.value.log

      def getTool(toolName: String): BuildTool = BuildTool.buildTools.getOrElse(
        toolName.toLowerCase,
        sys.error("Unsupported build tool: " + toolName)
      )

      val args = spaceDelimited("<tool> [<libname>]").parsed.toList

      val (tool: BuildTool, lib: String) = args match {
        case Nil                  => sys.error("Invalid arguments.")
        case tool :: Nil          => (getTool(tool), name.value)
        case tool :: lib :: other => (getTool(tool), lib)
      }

      log.info(s"Initializing native build with ${tool.name} configuration")
      val files = tool.initTemplate((nativeCompile / sourceDirectory).value, lib)
      files.foreach { file =>
        log.info("Wrote to " + file.getAbsolutePath)
      }
      files
    }
  )

  private def determinePlatform(): (String, String) = {
    try {
      val line =
        try {
          scala.io.Source.fromString(scala.sys.process.Process("uname -sm").!!).getLines().next()
        } catch {
          case _: Exception => sys.error("Error running `uname` command")
        }
      val parts = line.split(" ")
      if (parts.length != 2) {
        sys.error("Could not determine platform: 'uname -sm' returned unexpected string: " + line)
      } else {
        val arch = parts(1).toLowerCase.replaceAll("\\s", "")
        val kernel = parts(0).toLowerCase.replaceAll("\\s", "")
        (arch, kernel)
      }
    } catch {
      case _: Exception =>
        val os = System.getProperty("os.name").toLowerCase match {
          case s if s.contains("win") => "windows"
          case s if s.contains("mac") => "darwin"
          case _                      => "linux"
        }

        val arch = System.getProperty("os.arch").toLowerCase match {
          case "arm64" | "aarch64" => "arm64"
          case _                   => "x86_64"
        }

        (os, arch)
    }
  }

  override lazy val projectSettings = settings

}
