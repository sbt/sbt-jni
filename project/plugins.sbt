// cross-compile subprojects with differing scala versions
addSbtPlugin("com.eed3si9n" % "sbt-doge" % "0.1.5")

// testing for sbt plugins
libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
