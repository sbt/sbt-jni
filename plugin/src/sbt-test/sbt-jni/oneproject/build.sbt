ivyLoggingLevel := UpdateLogging.Quiet

enablePlugins(JniNative)

target in javah := (sourceDirectory in nativeCompile).value / "include"

