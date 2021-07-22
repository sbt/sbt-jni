ivyLoggingLevel := UpdateLogging.Quiet

lazy val root = (project in file(".")).aggregate(core, native)

lazy val core = project
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test)
  .settings(target in javah := (sourceDirectory in nativeCompile in native).value / "include")
  .dependsOn(native % Runtime)

lazy val native = project
  .settings(sourceDirectory in nativeCompile := sourceDirectory.value)
  .enablePlugins(JniNative)
