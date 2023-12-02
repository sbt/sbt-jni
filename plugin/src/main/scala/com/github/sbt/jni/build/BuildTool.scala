package com.github.sbt.jni
package build

import java.io.File
import java.nio.file.Files

import scala.io.Source

import sbt.Logger

trait BuildTool {

  /**
   * Name of this build tool.
   */
  def name: String

  /**
   * Detect if this build tool is configured in the given directory. E.g. for the Make build tool, this would return true if a Makefile is present in
   * the given directory.
   */
  def detect(baseDirectory: File): Boolean

  protected def templateMappings: Seq[(String, String)]

  /**
   * Initialize the given directory with a minimal, functioning configuration for this build tool. E.g. for the Make build tool, this would create a
   * Makefile in the given directory that is compatible with sbt-jni.
   * @return
   *   all created files
   */
  def initTemplate(baseDirectory: File, projectName: String): Seq[File] =
    for ((resource, name) <- templateMappings) yield {
      val resourceStream = this.getClass.getResourceAsStream(resource)

      if (resourceStream == null) sys.error(s"Template for $name not found.")

      val raw = Source.fromInputStream(resourceStream).mkString("")
      val replaced = raw.replaceAll("\\{\\{project\\}\\}", projectName)

      baseDirectory.mkdir()
      val out = baseDirectory.toPath().resolve(name)
      if (!Files.isDirectory(out.getParent)) {
        Files.createDirectories(out.getParent)
      }
      Files.write(out, replaced.getBytes)
      out.toFile()
    }

  /**
   * Actual tasks that can be perfomed on a specific configuration, such as configured in a Makefile.
   */
  trait Instance {

    /**
     * Invokes the native build tool's clean task
     */
    def clean(): Unit

    /**
     * Invokes the native build tool's main task, resulting in a single shared library file.
     * @param baseDirectory
     *   the directory where the native project is located
     * @param buildDirectory
     *   a directory from where the build is called, it may be used to store temporary files
     * @param targetDirectory
     *   the directory into which the native library is copied
     * @return
     *   the native library file
     */
    def library(
      targetDirectory: File
    ): List[File]

  }

  /**
   * Get an instance (build configuration) of this tool, in the specified directory.
   */
  def getInstance(baseDirectory: File, buildDirectory: File, logger: Logger): Instance

  /**
   * At least one produced library is expected.
   */
  def validate(list: List[File], logger: Logger): List[File] = {
    list match {
      case Nil =>
        sys.error(
          s"No files were created during compilation, " +
            s"something went wrong with the $name configuration."
        )
      case list @ _ :: Nil =>
        list
      case list =>
        logger.info(
          "More than one file was created during compilation: " +
            s"${list.map(_.getAbsolutePath).mkString(", ")}."
        )
        list
    }
  }
}

object BuildTool {
  lazy val buildTools: Map[String, BuildTool] = Map(
    CMake.name.toLowerCase -> CMake.release,
    Cargo.name.toLowerCase -> Cargo.release,
    Meson.name.toLowerCase -> Meson.release
  )
}
