import sbt._
import sbt.Keys._

object ScriptedHelper extends AutoPlugin {

  override def requires = empty
  override def trigger = allRequirements

  // set the platform explicitly, so `uname` is not used neither by the plugin nor by the native libraries loaders
  final val Platform = "custom-platform"
  System.setProperty("sbt.jni.platform", Platform)

  override def projectSettings = Seq(
    scalacOptions ++= Seq("-feature", "-deprecation"),
    crossScalaVersions := Seq("3.8.4", "2.13.18", "2.12.21"),
    scalaVersion := crossScalaVersions.value.head,
    javaOptions += s"-Dsbt.jni.platform=$Platform"
  )

}
