package ch.jodersky.sbt.jni
package plugins

import sbt._
import sbt.Keys._
import util.ByteCode

/** Adds `javah` header-generation functionality to projects. */
object JniJavah extends AutoPlugin {

  override def requires = plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {

    val javahClasses = taskKey[Set[String]](
      "Fully qualified names of classes containing native declarations."
    )

    val javah = taskKey[File](
      "Generate JNI headers. Returns the directory containing generated headers."
    )

  }
  import autoImport._

  lazy val mainSettings: Seq[Setting[_]] = Seq(

    javahClasses in javah := {
      val compiled: inc.Analysis = (compile in Compile).value
      val classFiles: Set[File] = compiled.relations.allProducts.toSet
      val nativeClasses = classFiles flatMap { file =>
        ByteCode.natives(file)
      }
      nativeClasses
    },

    target in javah := target.value / "include",

    fullClasspath in javah := fullClasspath.value,

    javah := {
      val out = (target in javah).value
      val jcp: Seq[File] = (fullClasspath in javah).value.map(_.data)
      val cp = jcp.mkString(sys.props("path.separator"))
      val log = streams.value.log

      val classes = (javahClasses in javah).value

      log.info("Headers will be generated to " + out.getAbsolutePath)
      for (clazz <- classes) {
        log.info("Generating header for " + clazz + "...")
        val parts = Seq(
          "javah",
          "-d", out.getAbsolutePath,
          "-classpath", cp,
          clazz
        )
        val cmd = parts.mkString(" ")
        val ev = Process(cmd) ! streams.value.log
        if (ev != 0) sys.error(s"Error occured running javah. Exit code: ${ev}")
      }
      out
    }
  )

  override lazy val projectSettings = inConfig(Compile)(mainSettings)

}
