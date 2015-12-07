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
   * @param baseDirectory the directory where the native project is located
   * @param buildDirectory a directory from where the build is called, it may be used to store temporary files
   * @param targetDirectory the directory into which the native library is copied
   * @return the native library file
   */
  def library(
    baseDirectory: File,
    targetDirectory: File,
    buildDirectory: File,
    log: Logger
  ): File

}
