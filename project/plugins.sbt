// cross-compile subprojects with differing scala versions
addSbtPlugin("com.eed3si9n" % "sbt-doge" % "0.1.5")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

// testing for sbt plugins
libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
