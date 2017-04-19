import sbt._
import sbt.Keys._

object ScriptedHelper extends AutoPlugin {

  override def requires = empty
  override def trigger = allRequirements

  override def projectSettings = Seq(
    scalacOptions ++= Seq("-feature", "-deprecation"),
    crossScalaVersions := Seq("2.12.2", "2.11.11", "2.10.6"),
    scalaVersion := crossScalaVersions.value.head
  )

}
