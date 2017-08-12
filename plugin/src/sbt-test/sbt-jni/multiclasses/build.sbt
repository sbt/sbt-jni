ivyLoggingLevel := UpdateLogging.Quiet

lazy val root = (project in file(".")).
  aggregate(core, native1, native2)

lazy val core = (project in file("core")).
  dependsOn(native1 % Runtime).
  dependsOn(native2 % Runtime).
  dependsOnRun(native1).
  dependsOnRun(native2)

lazy val native1 = (project in file("native1")).
  settings(sourceDirectory in nativeCompile := sourceDirectory.value).
  enablePlugins(JniNative)

lazy val native2 = (project in file("native2")).
  settings(sourceDirectory in nativeCompile := sourceDirectory.value).
  enablePlugins(JniNative)
