import sbt._
import sbt.Keys._

object ScriptedHelper extends AutoPlugin {

  override def requires = empty
  override def trigger = allRequirements

  override def projectSettings = Seq(
    scalacOptions ++= Seq("-feature", "-deprecation"),
    crossScalaVersions := Seq("3.7.1", "2.13.16", "2.12.20"),
    scalaVersion := crossScalaVersions.value.head
  )

}
