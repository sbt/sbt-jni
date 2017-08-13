package ch.jodersky.sbt.jni.plugins

import sbt._
import sbt.Keys._
import java.io.File

/** Adds an extension method `dependsOnRun` to projects, to work around an sbt
  * bug https://github.com/sbt/sbt/issues/3425 */
object RunDependencyFixPlugin extends AutoPlugin {

  override def requires = plugins.CorePlugin
  override def trigger = allRequirements

  object autoImport {

    val runClasspath = taskKey[Seq[sbt.internal.util.Attributed[File]]]("Classpath used in run task")

    def dependsOnRunSettings(project: Project) = Seq(
      runClasspath in Compile ++= (runClasspath in Compile in project).value,
      run := {
        Defaults.runTask(
          runClasspath in Compile,
          mainClass in Compile in run,
          runner in run
        ).evaluated
      }
    )

    implicit class RichProject(project: Project) {
      @deprecated("Temporary fix for https://github.com/sbt/sbt/issues/3425", "1.3.0")
      def dependsOnRun(other: Project) = {
        project.settings(dependsOnRunSettings(other): _*)
      }
    }

  }
  import autoImport._

  override def projectSettings = Seq(
    runClasspath in Compile := (fullClasspath in Compile).value
  )

}
