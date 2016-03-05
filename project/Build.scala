import sbt._
import sbt.Keys._

import bintray.BintrayPlugin.autoImport._
import sbtdoge.CrossPerProjectPlugin

object JniBuild extends Build {

  val scalaVersions = List("2.11.7", "2.12.0-M3", "2.10.5")

  val commonSettings = Seq(
    version := "0.4.3",
    organization := "ch.jodersky",
    licenses := Seq(("BSD New", url("http://opensource.org/licenses/BSD-3-Clause"))),
    scalacOptions ++= Seq("-deprecation", "-feature")
  )

  lazy val root = Project(
    id = "root",
    base = file("."),
    aggregate = Seq(
      library, plugin
    ),
    settings = Seq(
      publish := {},
      publishLocal := {},
      publishTo := Some(Resolver.file("Unused transient repository", target.value / "unusedrepo")) // make sbt-pgp happy
    )
  ).enablePlugins(CrossPerProjectPlugin)

  lazy val library = Project(
    id = "jni-library",
    base = file("jni-library"),
    settings = commonSettings ++ Seq(
      scalaVersion := scalaVersions.head,
      crossScalaVersions := scalaVersions
    )
  )

  lazy val plugin = Project(
    id = "sbt-jni",
    base = file("jni-plugin"),
    dependencies = Seq(library),
    settings = commonSettings ++ Seq(
      sbtPlugin := true,
      sourceGenerators in Compile += Def.task{
        val src = s"""|package ch.jodersky.sbt.jni
                      |
                      |private object Version {
                      |  final val PluginVersion = "${version.value}"
                      |}
                      |""".stripMargin
        val file = sourceManaged.value / "ch" / "jodersky" / "sbt" / "jni" / "Version.scala"
        IO.write(file, src)
        Seq(file)
      }.taskValue,
      libraryDependencies += "org.ow2.asm" % "asm" % "5.0.4",
      publishMavenStyle := false,
      bintrayRepository := "sbt-plugins",
      bintrayOrganization in bintray := None
    )
  )

}
