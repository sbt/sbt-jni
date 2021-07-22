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

    lazy val cmakeVersion = cmakeProcess("--version").lineStream.head
      .split("\\s+")
      .last
      .split("\\.") match {
      case Array(maj, min, rev) =>
        logger.info(s"Using CMake version $maj.$min.$rev")
        maj.toInt * 100 + min.toInt
      case _ => -1
    }

    def parallelOptions: Seq[String] =
      if (cmakeVersion >= 312)
        Seq("--parallel", parallelJobs.toString())
      else Seq.empty

    override def configure(target: File) = cmakeProcess(
      // disable producing versioned library files, not needed for fat jars
      s"-DCMAKE_INSTALL_PREFIX:PATH=${target.getAbsolutePath}",
      "-DCMAKE_BUILD_TYPE=Release",
      "-DSBT:BOOLEAN=true",
      cmakeVersion.toString,
      baseDirectory.getAbsolutePath
    )

    override def clean(): Unit = cmakeProcess(
      "--build",
      buildDirectory.getAbsolutePath,
      "--target",
      "clean"
    ).run(log)

    override def make(): ProcessBuilder = cmakeProcess(
      Seq("--build", buildDirectory.getAbsolutePath) ++ parallelOptions: _*
    )

    override def install(): ProcessBuilder = cmakeProcess(
      "--install",
      buildDirectory.getAbsolutePath
    )
  }

}
