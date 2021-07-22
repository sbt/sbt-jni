ivyLoggingLevel := UpdateLogging.Quiet

lazy val root = (project in file(".")).aggregate(core, native1, native2)

lazy val core = project
  .dependsOn(native1 % Runtime)
  .dependsOn(native2 % Runtime)

lazy val native1 = project
  .settings(nativeCompile / sourceDirectory := sourceDirectory.value)
  .enablePlugins(JniNative)

lazy val native2 = project
  .settings(nativeCompile / sourceDirectory := sourceDirectory.value)
  .enablePlugins(JniNative)
