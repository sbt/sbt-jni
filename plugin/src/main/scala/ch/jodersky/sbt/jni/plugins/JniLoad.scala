package ch.jodersky.sbt.jni
package plugins

import sbt._
import sbt.Keys._

object JniLoad extends AutoPlugin {

  override def requires = empty
  override def trigger = allRequirements

  lazy val settings: Seq[Setting[_]] = Seq(
    // Macro Paradise plugin and dependencies are needed to expand annotation macros.
    // Once expanded however, downstream projects don't need these dependencies anymore
    // (hence the "Provided" configuration).
    addCompilerPlugin(
      "org.scalamacros" % "paradise" % ProjectVersion.MacrosParadise cross CrossVersion.full
    ),
    libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided,
    libraryDependencies += "ch.jodersky" %% "sbt-jni-macros" % ProjectVersion.Macros % Provided
  )

  override def projectSettings = settings

}
