package ch.jodersky.sbt.jni
package build

import sbt._
import sys.process._

object CMake extends BuildTool with ConfigureMakeInstall {

  override val name = "CMake"

  override def detect(baseDirectory: File) = baseDirectory.list().contains("CMakeLists.txt")

  override protected def templateMappings = Seq(
    "/ch/jodersky/sbt/jni/templates/CMakeLists.txt" -> "CMakeLists.txt"
  )

  override def getInstance(baseDir: File, buildDir: File, logger: Logger) = new Instance {

    override def log = logger
    override def baseDirectory = baseDir
    override def buildDirectory = buildDir

    def cmakeProcess(args: String*): ProcessBuilder = Process("cmake" +: args, buildDirectory)

    override def configure(target: File) = cmakeProcess(
      // disable producing versioned library files, not needed for fat jars
      s"-DCMAKE_INSTALL_PREFIX:PATH=${target.getAbsolutePath}",
      "-DCMAKE_BUILD_TYPE=Release",
      "-DSBT:BOOLEAN=true",
      baseDirectory.getAbsolutePath
    )

    override def clean(): Unit = cmakeProcess(
      "--build", buildDirectory.getAbsolutePath,
      "--target", "clean"
    ) ! log

    override def make(): ProcessBuilder = cmakeProcess(
      "--build", buildDirectory.getAbsolutePath,
      "--parallel", parallelJobs.toString()
    )

    override def install(): ProcessBuilder = cmakeProcess(
      "--install", buildDirectory.getAbsolutePath
    )
  }

}
