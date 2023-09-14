ivyLoggingLevel := UpdateLogging.Quiet

lazy val root = (project in file(".")).aggregate(core, native)

lazy val core = project
  .settings(
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test,
    sbtJniCoreScope := Compile
  )
  .dependsOn(native % Runtime)

lazy val native = project
  .settings(nativeCompile / sourceDirectory := baseDirectory.value) // `baseDirectory`, not `sourceDirectory`
  .enablePlugins(JniNative)
