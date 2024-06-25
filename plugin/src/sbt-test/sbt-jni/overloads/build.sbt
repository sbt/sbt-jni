ivyLoggingLevel := UpdateLogging.Quiet

lazy val root = (project in file(".")).aggregate(core, native)

lazy val core = project
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test)
  .settings(javah / target := (native / nativeCompile / sourceDirectory).value / "include")
  .dependsOn(native % Runtime)

lazy val native = project
  .settings(nativeCompile / sourceDirectory := sourceDirectory.value)
  .enablePlugins(JniNative)
