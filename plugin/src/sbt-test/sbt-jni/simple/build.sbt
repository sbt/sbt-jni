ivyLoggingLevel := UpdateLogging.Quiet

lazy val root = (project in file(".")).
  aggregate(core, native)

lazy val core = (project in file("core")).
  settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0-RC4" % "test").
  settings(target in javah := (sourceDirectory in nativeCompile in native).value / "include").
  dependsOn(native % Runtime)

lazy val native = (project in file("native")).
  settings(sourceDirectory in nativeCompile := sourceDirectory.value).
  enablePlugins(JniNative)
