package ch.jodersky.sbt.jni
package build

import java.io.File

trait BuildTool {

  def name: String

  def detect(baseDirectory: File): Boolean

  def api: BuildToolApi

}
