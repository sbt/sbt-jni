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
    def multipleOutputs: Boolean
    def baseDirectory: File
    def buildDirectory: File

    def configure(targetDirectory: File): ProcessBuilder
    def make(): ProcessBuilder
    def install(): ProcessBuilder
    def clean(): Unit

    def parallelJobs: Int = java.lang.Runtime.getRuntime().availableProcessors()

    def library(
      targetDirectory: File
    ): List[File] = {

      val ev: Int = (
        configure(targetDirectory) #&& make() #&& install()
      ) ! log

      if (ev != 0) sys.error(s"Building native library failed. Exit code: ${ev}")

      val products: List[File] =
        (targetDirectory ** ("*.so" | "*.dylib")).get.filter(_.isFile).toList

      validate(products, multipleOutputs, log)
    }
  }

}
