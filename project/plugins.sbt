// cross-compile subprojects with differing scala versions
addSbtPlugin("com.eed3si9n" % "sbt-doge" % "0.1.5")

// testing for sbt plugins
libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value

// formatting
addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

// publishing
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
