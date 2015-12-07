package ch.jodersky.sbt.jni

import sbt._
import sbt.Keys._
import util.ByteCode

object JniJvm extends AutoPlugin {

  override def requires = plugins.JvmPlugin

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
      val classFiles: Set[File] = compile.value.relations.allProducts.toSet
      val nativeClasses = classFiles.flatMap { file =>
        ByteCode.natives(file)
      }
      nativeClasses
    },

    target in javah := target.value / "include",

    fullClasspath in javah := (fullClasspath in Compile).value,

    javah := {
      val out = (target in javah).value
      val jcp: Seq[File] = (fullClasspath in javah).value.map(_.data)
      val cp = jcp.mkString(sys.props("path.separator"))
      val log = streams.value.log

      val classes = (javahClasses in javah).value

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

  lazy val clientSettings = Seq(
    //enable enhanced native library extraction
    libraryDependencies += "ch.jodersky" %% "jni-library" % Version.PluginVersion,
    crossPaths := false, //don't need to appends scala version to native jars
    fork in run := true //fork new JVM as native libraries can only be loaded once
  )

  override lazy val projectSettings = inConfig(Compile)(mainSettings) ++ clientSettings

}
