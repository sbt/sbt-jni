ivyLoggingLevel := UpdateLogging.Quiet

lazy val root = (project in file(".")).aggregate(core, native)

lazy val core = project
  .settings(libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % Test)
  .settings(javah / target := (native / nativeCompile / sourceDirectory).value / "include")
  .settings(sbtJniCoreScope := Compile)
  .dependsOn(native % Runtime)

lazy val native = project
  .settings(nativeCompile / sourceDirectory := sourceDirectory.value)
  .enablePlugins(JniNative)
