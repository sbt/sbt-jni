enablePlugins(JniPlugin)

name := "jni-demo"

organization := "org.example"

baseDirectory in jni := (baseDirectory in ThisBuild).value / "native"

javahObjects in javah += "org.example.jni.demo.Library"

libraryDependencies += "ch.jodersky" %% "jni-library" % "0.1-SNAPSHOT"

//exportJars in run := true

//librearDependencies += "org.example" %% "demo" % "0.1"
//librearDependencies += "org.example" % "demo" % "0.1" % "runtime" classifier "native"
