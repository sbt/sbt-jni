package ch.jodersky.sbt.jni
package plugins

import sbt._
import sbt.Keys._

object JniLoad extends AutoPlugin {

  override def requires = empty
  override def trigger = allRequirements

  object autoImport {

    val macroProvided = settingKey[Boolean](
      "Determines if macro dependecy is Provided. The default value is true." +
        "if set to false the macro would be a runtime dependency (required for Scala 3.x)."
    )

  }

  import autoImport._

  lazy val settings: Seq[Setting[_]] = Seq(
    macroProvided := true,
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
      val scope = if (macroProvided.value) Provided else Compile
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) => Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value % scope)
        case _            => Seq()
      }
    },
    libraryDependencies += {
      val scope = if (macroProvided.value) Provided else Compile
      "ch.jodersky" %% "sbt-jni-macros" % ProjectVersion.Macros % scope
    },
    resolvers += Resolver.jcenterRepo
  )

  override def projectSettings = settings

}
