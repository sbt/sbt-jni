package ch.jodersky.sbt.jni
package plugins

import build._
import ch.jodersky.sbt.jni.util.OsAndArch
import sbt._
import sbt.Keys._

/** Wraps a native build system in sbt tasks. */
object JniNative extends AutoPlugin {

  object autoImport {

    //Main task, inspect this first
    val nativeCompile = taskKey[File](
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

  }
  import autoImport._

  val nativeBuildToolInstance = taskKey[BuildTool#Instance]("Get an instance of the current native build tool.")

  lazy val settings: Seq[Setting[_]] = Seq(

    // the value retruned must match that of `ch.jodersky.jni.PlatformMacros#current()` of project `macros`
    nativePlatform := {
      val osName = OsAndArch.OsName
      if(osName == OsAndArch.UnknownName){
          sLog.value.error("Error trying to determine operating system")
          sLog.value.warn(s"Setting osName to ${OsAndArch.UnknownName}")
      }
      val osArch = System.getProperty("os.arch")
      s"$osName-$osArch"
    },

    sourceDirectory in nativeCompile := sourceDirectory.value / "native",

    target in nativeCompile := target.value / "native" / (nativePlatform).value,

    nativeBuildTool := {
      val tools = Seq(CMake)

      val src = (sourceDirectory in nativeCompile).value

      val tool = if (src.exists && src.isDirectory) {
        tools.find(t => t detect src)
      } else {
        None
      }
      tool getOrElse sys.error("No supported native build tool detected. " +
        s"Check that the setting 'sourceDirectory in nativeCompile' (currently set to $src) " +
        "points to a directory containing a supported build script. Supported build tools are: " +
        tools.map(_.name).mkString(",")
      )

    },

    nativeBuildToolInstance := {
      val tool = nativeBuildTool.value
      val srcDir = (sourceDirectory in nativeCompile).value
      val buildDir = (target in nativeCompile).value / "build"
      IO.createDirectory(buildDir)
      tool.getInstance(
        baseDirectory = srcDir,
        buildDirectory = buildDir,
        logger = streams.value.log
      )
    },

    clean in nativeCompile := {
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
      val targetDir = (target in nativeCompile).value / "bin"
      val log = streams.value.log

      IO.createDirectory(targetDir)

      log.info(s"Building library with native build tool ${tool.name}")
      val lib = toolInstance.library(targetDir)
      log.success(s"Library built in ${lib.getAbsolutePath}")
      lib
    },

    // also clean native sources
    clean := {
      (clean in nativeCompile).value
      clean.value
    },

    nativeInit := {
      import complete.DefaultParsers._

      val log = streams.value.log

      def getTool(toolName: String): BuildTool = toolName.toLowerCase match {
        case "cmake" => CMake
        case _ => sys.error("Unsupported build tool: " + toolName)
      }

      val args = spaceDelimited("<tool> [<libname>]").parsed.toList

      val (tool: BuildTool, lib: String) = args match {
        case Nil => sys.error("Invalid arguments.")
        case tool :: Nil => (getTool(tool), name.value)
        case tool :: lib :: other => (getTool(tool), lib)
      }

      log.info(s"Initializing native build with ${tool.name} configuration")
      val files = tool.initTemplate((sourceDirectory in nativeCompile).value, lib)
      files foreach { file =>
        log.info("Wrote to " + file.getAbsolutePath)
      }
      files
    }

  )

  override lazy val projectSettings = settings

}
