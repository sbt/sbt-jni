package ch.jodersky.sbt.jni
package plugins

import sbt._
import sbt.Keys._

object JniLoad extends AutoPlugin {

  override def requires = empty
  override def trigger = allRequirements

  object autoImport {

    val sbtJniCoreProvided = settingKey[Boolean](
      "Determines if sbt-jni-core dependecy is Provided or not. The default value is set to true." +
        "If set to false, the sbt-jni-core would be a runtime dependency (required for Scala 3.x compatible syntax)."
    )

  }

  import autoImport._

  lazy val settings: Seq[Setting[_]] = Seq(
    sbtJniCoreProvided := true,
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
      val scope = if (sbtJniCoreProvided.value) Provided else Compile
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) => Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value % scope)
        case _            => Seq()
      }
    },
    libraryDependencies += {
      val scope = if (sbtJniCoreProvided.value) Provided else Compile
      "ch.jodersky" %% "sbt-jni-core" % ProjectVersion.Core % scope
    },
    resolvers += Resolver.jcenterRepo
  )

  override def projectSettings = settings

}
