package com.github.sbt.jni
package build

import sbt._
import sys.process._

object CMake extends BuildTool with ConfigureMakeInstall {

  override val name = "CMake"

  override def detect(baseDirectory: File) = baseDirectory.list().contains("CMakeLists.txt")

  override protected def templateMappings = Seq(
    "/com/github/sbt/jni/templates/CMakeLists.txt" -> "CMakeLists.txt"
  )

  override def getInstance(baseDir: File, buildDir: File, logger: Logger) = new Instance {

    override def log = logger
    override def baseDirectory = baseDir
    override def buildDirectory = buildDir

    def cmakeProcess(args: String*): ProcessBuilder = Process("cmake" +: args, buildDirectory)

    lazy val cmakeVersion =
      cmakeProcess("--version").lineStream.head
        .split("\\s+")
        .last
        .split("\\.") match {
        case Array(maj, min, rev) =>
          logger.info(s"Using CMake version $maj.$min.$rev")
          maj.toInt * 100 + min.toInt
        case _ => -1
      }

    def parallelOptions: Seq[String] =
      if (cmakeVersion >= 312) Seq("--parallel", parallelJobs.toString())
      else Seq.empty

    override def configure(target: File) = {
      // disable producing versioned library files, not needed for fat jars
      val args = Seq(s"-DCMAKE_INSTALL_PREFIX:PATH=${target.getAbsolutePath}", "-DCMAKE_BUILD_TYPE=Release", "-DSBT:BOOLEAN=true") ++ scala.sys.props
        .get("java.home")
        .toSeq
        .map(jh => s"-DJAVA_HOME=${jh}") ++ Seq(cmakeVersion.toString, baseDirectory.getAbsolutePath)
      cmakeProcess(args: _*)
    }

    override def clean(): Unit = cmakeProcess(
      "--build",
      buildDirectory.getAbsolutePath,
      "--target",
      "clean"
    ).run(log)

    override def make(): ProcessBuilder = cmakeProcess(
      Seq("--build", buildDirectory.getAbsolutePath) ++ parallelOptions: _*
    )

    override def install(): ProcessBuilder =
      // https://cmake.org/cmake/help/v3.15/release/3.15.html#id6
      // Beginning with version 3.15, CMake introduced the install switch
      if (cmakeVersion >= 315) cmakeProcess("--install", buildDirectory.getAbsolutePath)
      else Process("make install", buildDirectory)
  }

}
