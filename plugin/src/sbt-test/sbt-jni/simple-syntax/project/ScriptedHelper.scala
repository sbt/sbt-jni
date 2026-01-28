import sbt._
import sbt.Keys._

object ScriptedHelper extends AutoPlugin {

  override def requires = empty
  override def trigger = allRequirements

  override def projectSettings = Seq(
    scalacOptions ++= Seq("-feature", "-deprecation"),
    crossScalaVersions := Seq("3.8.1", "2.13.18", "2.12.21"),
    scalaVersion := crossScalaVersions.value.head
  )

}
