// testing for sbt plugins
libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.6.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")
