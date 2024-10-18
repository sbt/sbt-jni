package com.github.sbt.jni.build

import sbt._

import java.io.File
import scala.sys.process._

class Cargo(protected val configuration: Seq[String]) extends BuildTool {

  val name: String = Cargo.name

  def detect(baseDirectory: File): Boolean =
    baseDirectory.list().contains("Cargo.toml")

  protected def templateMappings: List[(String, String)] = List(
    "/com/github/sbt/jni/templates/Cargo.toml" -> "Cargo.toml"
  )

  def release: Boolean = configuration.exists(_.toLowerCase.contains("release"))

  def getInstance(baseDirectory: File, buildDirectory: File, logger: sbt.Logger, multipleOutputs: Boolean): Instance =
    new Instance(baseDirectory, logger, multipleOutputs)

  class Instance(protected val baseDirectory: File, protected val logger: sbt.Logger, protected val multipleOutputs: Boolean) extends super.Instance {
    // IntelliJ friendly logger, IntelliJ doesn't start tests if a line is printed as "error", which Cargo does for regular output
    protected val log: ProcessLogger = new ProcessLogger {
      def out(s: => String): Unit = logger.info(s)
      def err(s: => String): Unit = logger.warn(s)
      def buffer[T](f: => T): T = f
    }

    def clean(): Unit = Process("cargo clean", baseDirectory) ! log

    def library(targetDirectory: File): List[File] = {
      val configurationString = (configuration ++ Seq("--target-dir", targetDirectory.getAbsolutePath.backslashed)).mkString(" ").trim
      val ev =
        Process(
          s"cargo build $configurationString",
          baseDirectory
        ) ! log
      if (ev != 0) sys.error(s"Building native library failed. Exit code: $ev")

      val subdir = if (release) "release" else "debug"
      val products: List[File] =
        (targetDirectory / subdir * ("*.so" | "*.dylib")).get().filter(_.isFile).toList

      validate(products, multipleOutputs, logger)
    }
  }
}

object Cargo {
  val name: String = "Cargo"
  val DEFAULT_CONFIGURATION = Seq("--release")

  /**
   * List of cargo parameters passed, target-dir is not configurable.
   */
  def make(configuration: Seq[String] = DEFAULT_CONFIGURATION): BuildTool = new Cargo(configuration)

  /**
   * Cargo build tool, with the `--release` flag.
   */
  lazy val release: BuildTool = make()
}
