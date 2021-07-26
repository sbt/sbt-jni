package com.github.sbt.jni
package plugins

import java.net.URI
import java.nio.file.Paths

import collection.JavaConverters._
import util.BytecodeUtil

import sbt._
import sbt.Keys._
import xsbti.compile.CompileAnalysis

/**
 * Adds `javah` header-generation functionality to projects.
 */
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
    javah / javahClasses := {
      import xsbti.compile._
      val compiled: CompileAnalysis = (Compile / compile).value
      val classFiles: Set[File] = compiled
        .readStamps()
        .getAllProductStamps()
        .asScala
        .keySet
        .map { vf =>
          (vf.names() match {
            case Array(prefix, first, more @ _*) if prefix.startsWith("${") =>
              Paths.get(first, more: _*)
            case _ =>
              Paths.get(URI.create("file:///" + vf.id().stripPrefix("/")))
          }).toFile()
        }
        .toSet
      val nativeClasses = classFiles.flatMap { file =>
        BytecodeUtil.nativeClasses(file)
      }
      nativeClasses
    },
    javah / target := target.value / "native" / "include",
    javah := {
      val out = (javah / target).value

      val task = new com.github.sbt.jni.javah.JavahTask

      val log = streams.value.log

      val classes = (javah / javahClasses).value
      if (classes.nonEmpty) {
        log.info("Headers will be generated to " + out.getAbsolutePath)
      }
      classes.foreach(task.addClass(_))

      // fullClasspath can't be used here since it also generates resources. In
      // a project combining JniJavah and JniPackage, we would have a chicken-and-egg
      // problem.
      val jcp: Seq[File] = (Compile / dependencyClasspath).value.map(_.data) :+ (Compile / classDirectory).value
      jcp.foreach(cp => task.addClassPath(cp.toPath))

      task.addRuntimeSearchPath()
      task.setOutputDir(Paths.get(out.getAbsolutePath))
      task.run()

      out
    }
  )

  override lazy val projectSettings = mainSettings

}
