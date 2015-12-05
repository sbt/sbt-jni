package ch.jodersky.sbt.jni

import build.BuildTool

import ch.jodersky.jni.Platform
import sbt._

object Keys {

  val jni = taskKey[File]("Builds a native library (by calling the native build tool).")
  val jniClean = taskKey[Unit]("Cleans the native build directory.")

  val jniPlatform = settingKey[Platform]("Platform of the system this build is running on.")
  val jniBuildTool = taskKey[BuildTool]("The build tool to be used when building a native library.")

  //bundle
  val jniLibraryPath = settingKey[String]("Foo")
  val packageJni = taskKey[File]("Package native librraies into a fat jar.")

  val javahClasses = settingKey[Seq[String]]("Fully qualified names of classes containing native declarations.")
  val javahObjects = settingKey[Seq[String]]("Fully qualified names of singleton objects containing native declarations.")
  val javah = taskKey[File]("Generate JNI headers. Returns the directory containing generated headers.")


  //ivy
  //libraryDependencies += "com.github.jodersky" % "flow" % "2.4" extra("platform", "all") artifact("libflow", "so")

  //maven
  //libraryDependencies += "com.github.jodersky" % "flow" % "2.4" classifier "native"



  //Wraps tasks associated to an existing native build tool
  //val Native = config("native")

  //Extra tasks in native
 // val buildTool = settingKey[BuildTool]("The native build tool used.")
  //val platform = settingKey[Platform]("Platform of the system this build is being run on.")

  // organization = org.example
  // name = foo-bar
  // libraryPrefix = organization + "." + normalize(name)
  // libraryPrefix = org/example/foo/bar
  // libraryPrefix = org/example/foo/bar/native/<platform>/libraries


  //val libraryPath = settingKey[String](
  //  "A slash (/) seprated path that specifies where native libraries should be stored (in a jar).")
  //val libraryResourceDirectory = settingKey[File]("")
  
  //Javah
  
}
