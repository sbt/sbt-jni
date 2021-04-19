ivyLoggingLevel := UpdateLogging.Quiet

addSbtPlugin(System.getProperty("plugin.organization") % "sbt-jni" % System.getProperty("plugin.version"))
