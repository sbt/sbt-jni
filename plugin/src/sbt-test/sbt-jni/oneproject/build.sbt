ivyLoggingLevel := UpdateLogging.Quiet

enablePlugins(JniNative)

javah / target := (nativeCompile / sourceDirectory).value / "include"

