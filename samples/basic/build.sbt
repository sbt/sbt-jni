val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "ch.jodersky"
)

lazy val main = Project(
  id = "sample-basic-main",
  base = file("basic-main"),
  settings = commonSettings ++ Seq(
    target in (Compile, javah) :=
      (sourceDirectory in native).value / "include"
  ),
  dependencies = Seq(
    native % Runtime
  )
).enablePlugins(JniJvm)

lazy val native = Project(
  id = "sample-basic-native",
  base = file("basic-native"),
  settings = commonSettings ++ Seq(
    jniLibraryPath in (Compile, jni) := "/ch/jodersky/jni/basic"
  )
).enablePlugins(JniNative)
