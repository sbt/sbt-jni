val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "ch.jodersky"
)

lazy val root = Project(
  id = "root",
  base = file("."),
  aggregate = Seq(core, native)
)

lazy val core = Project(
  id = "basic-core",
  base = file("basic-core"),
  settings = commonSettings ++ Seq(
    target in javah in Compile := (sourceDirectory in native).value / "include"
  ),
  dependencies = Seq(
    native % Runtime
  )
).enablePlugins(JniLoading)

lazy val native = Project(
  id = "basic-native",
  base = file("basic-native"),
  settings = commonSettings ++ Seq(
    //enableNativeCompilation in Compile := false,
    sourceDirectory in nativeCompile in Compile := sourceDirectory.value,
    nativeLibraryPath in Compile := "/ch/jodersky/jni/basic/native"
  )
).enablePlugins(JniNative)
