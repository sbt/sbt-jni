import sbt._
import sbt.Keys._

object NativeUtilsBuild extends Build {

  val commonSettings = Seq(
    version := "0.1-SNAPSHOT",
    organization := "ch.jodersky",
    scalacOptions ++= Seq("-deprecation", "-feature")
  )

  lazy val root = Project(
    id = "root",
    base = file("."),
    aggregate = Seq(
      library, plugin
    ),
    settings = Seq(
      publish := {}
    )
  )

  lazy val library = Project(
    id = "jni-library",
    base = file("jni-library"),
    settings = commonSettings
  )

  lazy val plugin = Project(
    id = "sbt-jni",
    base = file("jni-plugin"),
    settings = commonSettings ++ Seq(sbtPlugin := true),
    dependencies = Seq(library)
  )

}
