package ch.jodersky.sbt.jni
package build

import java.io.File
import sbt.Logger


trait BuildToolApi {

  /** Invokes the native build tool's clean task */
  def clean(baseDirectory: File, log: Logger): Unit 

  /**
    * Invokes the native build tool's main task, resulting in a single shared
    * library file.
    */
  def library(
    baseDirectory: File,
    targetDirectory: File,
    log: Logger
  ): File

}
