import scala.sys.process._

val scalaVersions = Seq("3.3.1", "2.13.13", "2.12.18", "2.11.12")
val macrosParadiseVersion = "2.1.1"

ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / organization := "com.github.sbt"
ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-Xfatal-warnings")
ThisBuild / licenses := Seq(("BSD New", url("http://opensource.org/licenses/BSD-3-Clause")))
ThisBuild / homepage := Some(url("https://github.com/jodersky/sbt-jni"))
ThisBuild / developers := List(
  Developer(
    "jodersky",
    "Jakob Odersky",
    "jakob@odersky.com",
    url("https://jakob.odersky.com")
  ),
  Developer(
    "pomadchin",
    "Grigory Pomadchin",
    "@pomadchin",
    url("https://github.com/pomadchin")
  )
)

lazy val root = (project in file("."))
  .aggregate(core, plugin)
  .settings(
    publish := {},
    publishLocal := {},
    // make sbt-pgp happy
    publishTo := Some(Resolver.file("Unused transient repository", target.value / "unusedrepo")),
    addCommandAlias("test-plugin", ";+core/publishLocal;scripted")
  )

lazy val core = project
  .settings(
    name := "sbt-jni-core",
    scalaVersion := scalaVersions.head,
    crossScalaVersions := scalaVersions,
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) =>
          Seq(
            "org.scala-lang" % "scala-compiler" % scalaVersion.value % Provided,
            "org.scala-lang" % "scala-reflect" % scalaVersion.value
          )
        case _ => Seq("org.scala-lang" %% "scala3-compiler" % scalaVersion.value % Provided)
      }
    },
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n < 13 =>
          Seq(compilerPlugin(("org.scalamacros" % "paradise" % macrosParadiseVersion).cross(CrossVersion.full)))
        case _ => Seq()
      }
    },
    Compile / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 13 => Seq("-Ymacro-annotations")
        case _                       => Seq()
      }
    }
  )

lazy val plugin = project
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-jni",
    libraryDependencies ++= Seq("org.ow2.asm" % "asm" % "9.6", "org.scalatest" %% "scalatest" % "3.2.18" % Test),
    // make project settings available to source
    Compile / sourceGenerators += Def.task {
      val src = s"""|/* Generated by sbt */
                    |package com.github.sbt.jni
                    |
                    |private[jni] object ProjectVersion {
                    |  final val MacrosParadise = "${macrosParadiseVersion}"
                    |  final val Core = "${version.value}"
                    |}
                    |""".stripMargin
      val file = sourceManaged.value / "com" / "github" / "sbt" / "jni" / "ProjectVersion.scala"
      IO.write(file, src)
      Seq(file)
    }.taskValue,
    scriptedLaunchOpts := Seq(
      "-Dplugin.version=" + version.value,
      "-Xmx2g",
      "-Xss2m"
    )
  )
