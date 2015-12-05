package ch.jodersky.sbt.jni

import sbt._

object JniPlugin extends AutoPlugin {

  override def requires = plugins.JvmPlugin

  lazy val autoImport = Keys
  import autoImport._

  override lazy val projectSettings =
    Defaults.buildSettings ++
    inConfig(Runtime)(Defaults.bundleSettings) ++
    Defaults.clientSettings
  //++inConfig(Runtime)(Defaults.bundleSettings)
   

}
