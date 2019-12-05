package ch.jodersky.sbt.jni
package plugins

import java.nio.file.{Path, Paths}

import collection.JavaConverters._
import util.BytecodeUtil
import java.util

import sbt._
import sbt.Keys._

/** Adds `javah` header-generation functionality to projects. */
object JniJavah extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {

    val javahClasses = taskKey[Set[String]](
      "Finds fully qualified names of classes containing native declarations."
    )

    val javah = taskKey[File](
      "Generate JNI headers. Returns the directory containing generated headers."
    )

  }
  import autoImport._

  lazy val mainSettings: Seq[Setting[_]] = Seq(

    javahClasses in javah := {
      import xsbti.compile._
      val compiled: CompileAnalysis = (compile in Compile).value
      val classFiles: Set[File] = compiled.readStamps().getAllProductStamps()
        .asScala.keySet.toSet
      val nativeClasses = classFiles flatMap { file =>
        BytecodeUtil.nativeClasses(file)
      }
      nativeClasses
    },

    target in javah := target.value / "native" / "include",

    javah := {
      val out = (target in javah).value

      // fullClasspath can't be used here since it also generates resources. In
      // a project combining JniJavah and JniPackage, we would have a chicken-and-egg
      // problem.
      val jcp: Seq[File] = (dependencyClasspath in Compile).value.map(_.data) ++ {
        (compile in Compile).value; Seq((classDirectory in Compile).value)
      }

      val log = streams.value.log

      val classes = (javahClasses in javah).value
      if (classes.nonEmpty) {
        log.info("Headers will be generated to " + out.getAbsolutePath)
      }

      val task = new ch.jodersky.sbt.jni.javah.JavahTask
      classes.foreach(task.addClass(_))  
      jcp.map(_.toPath).foreach(task.addClassPath(_))
      task.addRuntimeSearchPath()
      task.setOutputDir(Paths.get(out.getAbsolutePath))
      task.run()

      out
    }
  )

  override lazy val projectSettings = mainSettings

}
