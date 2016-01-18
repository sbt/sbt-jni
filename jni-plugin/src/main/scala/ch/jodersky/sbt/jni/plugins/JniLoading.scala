package ch.jodersky.sbt.jni
package plugins

import sbt._
import sbt.Keys._
import util.ByteCode

/**
 * Enables loading native libraries from the classpath, typically created
 * from a project using JniPackaging.
 */
object JniLoading extends AutoPlugin {

  override def requires = plugins.JvmPlugin

  lazy val settings = Seq(

    //enable enhanced native library extraction
    libraryDependencies += "ch.jodersky" %% "jni-library" % Version.PluginVersion,

    //fork new JVM, since native libraries can only be loaded once
    fork in run := true

  )

  override lazy val projectSettings = settings

}
