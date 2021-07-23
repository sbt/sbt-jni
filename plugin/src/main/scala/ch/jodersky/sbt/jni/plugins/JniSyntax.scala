package ch.jodersky.sbt.jni
package plugins

import sbt._
import sbt.Keys._

object JniSyntax extends AutoPlugin {

  lazy val settings: Seq[Setting[_]] = Seq(
    // Macro Paradise plugin and dependencies are needed to expand annotation macros.
    // Once expanded however, downstream projects don't need these dependencies anymore
    // (hence the "Provided" configuration).
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 13 => Seq()
        case Some((2, n)) =>
          Seq(compilerPlugin(("org.scalamacros" % "paradise" % ProjectVersion.MacrosParadise).cross(CrossVersion.full)))
        case _ => Seq()
      }
    },
    Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 13 => Seq("-Ymacro-annotations")
        case _                       => Seq()
      }
    },
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) => Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value)
        case _            => Seq()
      }
    },
    libraryDependencies += "ch.jodersky" %% "sbt-jni-core" % ProjectVersion.Macros,
    resolvers += Resolver.jcenterRepo
  )

  override def projectSettings = settings

}
