# SBT-JNI
Making JNI tolerable.

Work in progress...

## Overview of JNI
Java Native Interface (JNI), is a standard interface to allow programs written in a JVM language to interact with native code. Creating a program that uses JNI is usually a multi-step process:

1. The JVM side of the program is written (in Java or Scala for example) and interfacing methods declared as native.
2. JVM sources are compiled.
3. The program "javah" is run to generate C header files for classes containing native declarations.
4. These header files may be included from actual native source files that implement the native methods.
5. Native sources are compiled into a native library.

Running the program also requires special steps:

1. Supply the native library to the program's library path.
2. Run the program, making sure to load the native library (through `System.load()`) before calling any native methods.

Finally, since platform independence is lost with native libraries, publishing an application or library becomes harder:

1. Native libraries must be compiled and made available for all supported platforms.
2. Loading the correct library on a given platform has to be managed somehow.

All in all, using JNI impairs platform-independence and thus a considerable amount of the JVM's advantages regarding ease of development and distribution.

## Plugin Overview
As described in the previous paragraph, using JNI adds a number of burdens to developing applications. The problems can be divided into two groups, both of which this plugin attempts to address:

- Build problems, related to the manual work involved with synchronizing JVM and native sources and running applications locally.
- Distribution problems, related to the way libraries and applications can be deployed.

[how the plugin addresses issues]
[sbt wrapper for native build tools]

## Usage

Depend on plugin `project/plugins.sbt`:
```scala
addSbtPlugin("ch.jodersky" % "sbt-jni" % "0.2")
```

Create sbt project containing JVM sources:
```scala
lazy val main = Project("main", file("main)).enablePlugins(JniJvm).dependsOn(native % Runtime) 
```

Create sbt project containing native sources:
```scala
lazy val native = Project("native", file("native")).dependsOn(JniNative) 
```

### Keys

```
javah
jni
```

### Common settings

## Examples
