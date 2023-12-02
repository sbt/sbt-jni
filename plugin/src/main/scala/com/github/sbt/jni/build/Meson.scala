package com.github.sbt.jni
package build

import sbt._
import sys.process._

class Meson(protected val configuration: Seq[String]) extends BuildTool with ConfigureMakeInstall {

  override val name = Meson.name

  override def detect(baseDirectory: File) = baseDirectory.list().contains("meson.build")

  override protected def templateMappings = Seq(
    "/com/github/sbt/jni/templates/meson.build" -> "meson.build",
    "/com/github/sbt/jni/templates/meson.options" -> "meson.options"
  )

  override def getInstance(baseDir: File, buildDir: File, logger: Logger) = new Instance {

    override def log = logger
    override def baseDirectory = baseDir
    override def buildDirectory = buildDir

    def mesonProcess(args: String*): ProcessBuilder = Process("meson" +: args, buildDirectory)

    lazy val mesonVersion = mesonProcess("--version").lineStream.head.replace('.', '_')

    lazy val mesonBuildDir = buildDirectory / mesonVersion

    override def configure(target: File) = {
      mesonProcess(
        Seq("setup", "--prefix", target.getAbsolutePath) ++ configuration ++ Seq(
          mesonVersion,
          baseDirectory.getAbsolutePath
        ): _*
      )
    }

    override def clean(): Unit = mesonProcess(
      "compile",
      "-C",
      mesonBuildDir.getAbsolutePath,
      "--clean"
    ).run(log)

    override def make(): ProcessBuilder = mesonProcess(
      "compile",
      "-C",
      mesonBuildDir.getAbsolutePath,
      "--jobs",
      parallelJobs.toString
    )

    override def install(): ProcessBuilder = mesonProcess(
      "install",
      "-C",
      mesonBuildDir.getAbsolutePath
    )
  }

}

object Meson {
  val name = "Meson"
  val DEFAULT_CONFIGURATION = Seq("--buildtype=release", "-Dsbt=true")

  /**
   * Meson setup configuration arguments.
   */
  def make(configuration: Seq[String] = DEFAULT_CONFIGURATION): BuildTool = new Meson(configuration)

  /**
   * Meson build tool, with the release build type.
   */
  lazy val release: BuildTool = make()
}
