package com.github.sbt.jni
package build

import java.io.File
import sbt._
import sys.process._

trait ConfigureMakeInstall { self: BuildTool =>

  /* API for native build tools that use a standard 'configure && make && make install' process,
   * where the configure step is left ab
   stract. */
  trait Instance extends self.Instance {

    def log: Logger
    def baseDirectory: File
    def buildDirectory: File

    def configure(targetDirectory: File): ProcessBuilder
    def make(): ProcessBuilder
    def install(): ProcessBuilder
    def clean(): Unit

    def parallelJobs: Int = java.lang.Runtime.getRuntime().availableProcessors()

    def library(
      targetDirectory: File
    ): File = {

      val ev: Int = (
        configure(targetDirectory) #&& make() #&& install()
      ) ! log

      if (ev != 0) sys.error(s"Building native library failed. Exit code: ${ev}")

      val products: List[File] =
        (targetDirectory ** ("*.so" | "*.dylib")).get.filter(_.isFile).toList

      // only one produced library is expected
      products match {
        case Nil =>
          sys.error(
            s"No files were created during compilation, " +
              s"something went wrong with the ${name} configuration."
          )
        case head :: Nil =>
          head
        case head :: tail =>
          log.warn(
            "More than one file was created during compilation, " +
              s"only the first one (${head.getAbsolutePath}) will be used."
          )
          head
      }
    }
  }

}
