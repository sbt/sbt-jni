package ch.jodersky.sbt.jni
package build

import java.io.File
import sbt._

/** Native build tools relying on a standard 'configure && make && make install' process */
trait ConfigureMakeInstall extends BuildToolApi {

  override def clean(baseDirectory: File, log: Logger) = Process("make clean", baseDirectory) ! log

  def configure(baseDirectory: File, buildDirectory: File, targetDirectory: File): ProcessBuilder

  def make(baseDirectory: File, buildDirectory: File, targetDirectory: File): ProcessBuilder = Process("make", buildDirectory)

  def install(baseDirectory: File, buildDirectory: File, targetDirectory: File): ProcessBuilder = Process("make install", buildDirectory)

  override def library(
    baseDirectory: File,
    buildDirectory: File,
    targetDirectory: File,
    log: Logger
  ): File = {

    val ev = (
      configure(baseDirectory, buildDirectory, targetDirectory) #&&
      make(baseDirectory, buildDirectory, targetDirectory) #&&
      install(baseDirectory, buildDirectory, targetDirectory)
    ) ! log

    if (ev != 0) sys.error(s"Building native library failed. Exit code: ${ev}")

    val products: List[File] = (targetDirectory ** ("*" -- "*.la")).get.filter(_.isFile).toList

    //only one produced library is expected
    products match {
      case Nil =>
        sys.error("No files were created during compilation, " +
          "something went wrong with the autotools configuration.")
      case head :: Nil =>
        head
      case head :: tail =>
        log.warn("More than one file was created during compilation, " +
          s"only the first one (${head.getAbsolutePath}) will be used.")
        head
    }
  }
}

