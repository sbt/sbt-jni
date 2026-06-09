package com.github.sbt.jni
package plugins

import java.nio.file.Paths

import util.BytecodeUtil

import com.github.sbt.jni.plugins.JniPluginCompat._
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

  lazy val mainSettings: Seq[Setting[?]] = Seq(
    javah / javahClasses := Def.uncached {
      val compiled: CompileAnalysis = (Compile / compile).value
      // resolve product class files through the build's FileConverter so that virtual-file
      // roots such as `${OUT}` are expanded to real paths (works on both sbt 1.x and sbt 2.x)
      val converter = fileConverter.value
      val classFiles: Set[File] = JniPluginCompat
        .productKeys(compiled)
        .map(vf => converter.toPath(vf).toFile)
        .toSet
      val nativeClasses = classFiles.flatMap { file =>
        BytecodeUtil.nativeClasses(file)
      }
      nativeClasses
    },
    javah / target := target.value / "native" / "include",
    javah := Def.uncached {
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
      val converter = fileConverter.value
      val jcp: Seq[File] =
        (Compile / dependencyClasspath).value.map(JniPluginCompat.toFile(_, converter)) :+ (Compile / classDirectory).value
      jcp.foreach(cp => task.addClassPath(cp.toPath))

      task.addRuntimeSearchPath()
      task.setOutputDir(Paths.get(out.getAbsolutePath))
      task.run()

      out
    }
  )

  override lazy val projectSettings = mainSettings

}
