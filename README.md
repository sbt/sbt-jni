[![Build Status](https://travis-ci.org/jodersky/sbt-jni.svg?branch=master)](https://travis-ci.org/jodersky/sbt-jni)
[![Download](https://api.bintray.com/packages/jodersky/sbt-plugins/sbt-jni/images/download.svg) ](https://bintray.com/jodersky/sbt-plugins/sbt-jni/_latestVersion)

# sbt-jni

A suite of sbt plugins for simplifying creation and distribution of JNI programs.

## Motivation
Java Native Interface (JNI), is a framework that enables programs written in a JVM language to interact with native code and vice-versa. Such programs can be divided into two logical parts: the JVM part, consisting of sources that will be compiled to bytecode (e.g. Scala or Java), and the native part, consisting of sources that will be compiled to machine-native code (e.g. C, C++ or assembly).

Using native code can be beneficial in some situations: it can, for example, provide raw performance boosts or enable otherwise infeasable features such as interaction with peripherals. However, it also adds a few layers of complexities, most notably:

- Compilation: the project is divided into two parts, each of which require separate compilation.
- Portability: native binaries only run on the platform on which they were compiled.
- Distribution: native binaries must be made available and packaged for every supported platform.

The second point, portability, is inherent to JNI and thus unavoidable. However the first and last points can be greatly simplified with the help of build tools.

## Plugin Summary

| Plugin     | Description                                                                                            |
|------------|--------------------------------------------------------------------------------------------------------|
| JniJavah   | Adds support for generating headers from classfiles that have `@native` methods.                       |
| JniLoad    | Makes `@nativeLoader` annotation available, that injects code to transparently load native libraries.  |
| JniNative  | Adds sbt wrapper tasks around native build tools to ease building and integrating native libraries.    |
| JniPackage | Packages native libraries into multi-platform fat jars. No more manual library path configuration!     |

All plugins are made available by adding
```scala
resolvers += Resolver.jcenterRepo

addSbtPlugin("ch.jodersky" % "sbt-jni" % "1.0.0-RC1")
```
to `project/plugins.sbt`.

Note that most plugins are enabled in projects by default. Disabling their functionality can be achieved by adding `disablePlugin(<plugin>)` to the corresponding project definition (for example, to disable packaging native libraries).

## Plugin Details

### JniJavah

| Enabled                        | Source        |
|--------------------------------|---------------|
| automatic, for all projects    | [JniJavah.scala](plugin/src/main/scala/ch/jodersky/sbt/jni/plugins/JniJavah.scala)|

This plugin wraps the JDK `javah` command.

Run `sbt-javah` to generate C header files with prototypes for any methods marked as native.
E.g. the following scala class
```scala
package org.example
class Adder(val base: Int) {
  @native def plus(term: Int): Int // implemented in a native library
}
```
will yield this prototype
```c
/*
 * Class:     org_example_Adder
 * Method:    plus
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_example_Adder_plus
  (JNIEnv *, jobject, jint);

```

The header output directory can be configured
```
target in javah := <dir> // defaults to target/native/include
```

Note that native methods declared both in Scala and Java are supported. Whereas Scala uses the `@native` annotation, Java uses the
`native` keyword.

### JniLoad
| Enabled                        | Source        |
|--------------------------------|---------------|
| automatic, for all projects    | [JniLoad.scala](plugin/src/main/scala/ch/jodersky/sbt/jni/plugins/JniLoad.scala) |

This plugin enables loading native libraries in a safe and transparent manner to the developer (no more explicit, static `System.load("library")` calls required). It does so by providing a class annotation which injects native loading code to all its annottees. Furthermore, in case a native library is not available on the current `java.library.path`, the code injected by the annotation will fall back to loading native libraries packaged according to the rules of `JniPackage` and available on the classpath.

Example use:
```scala
import ch.jodersky.sbt.jni.nativeLoader

// By adding this annotation, there is no need to call
// System.load("adder0") before accessing native methods.
@nativeLoader("adder0")
class Adder(val base: Int) {
  @native def plus(term: Int): Int // implemented in libadder0.so
}

// The application feels like a pure Scala app.
object Main extends App {
  (new Adder(0)).plus(1)
}
```

Note: this plugin is just shorthand for adding `sbt-jni-macros` and `scala macros paradise` projects as provided dependencies.
Projects must use Scala versions 2.11 or 2.12.0-M4.

See the [annotation's implementation](macros/src/main/scala/ch/jodersky/sbt/jni/annotations.scala) for details about the injected code.

### JniNative
| Enabled                        | Source        |
|--------------------------------|---------------|
| manual                         | [JniNative.scala](plugin/src/main/scala/ch/jodersky/sbt/jni/plugins/JniNative.scala) |

TODO: explanation

### JniPackage
| Enabled                        | Source        |
|--------------------------------|---------------|
| automatic, when JniNative enabled | [JniPackage.scala](plugin/src/main/scala/ch/jodersky/sbt/jni/plugins/JniPackage.scala) |

TODO: explanation

## Canonical Use

1. Define separate sub-projects for JVM and native sources. In `myproject/build.sbt`:

   ```scala
   lazy val core = project in file("myproject-core"). // contains regular jvm sources and @native methods
     dependsOn(native % Runtime) // natives only required for running, compilation can be done without

   lazy val native = project in file("myproject-native"). // contains native sources
     enablePlugin(JniNative)
   ```
   Note that separate projects are not strictly required. They are recommended nevertheless, as a portability-convenience tradeoff: programs
   written in a jvm language are expected to run anywhere without recompilation, but including native libraries in jars limits this portability
   to only platforms of the packaged libraries.

2. Initialize the native build tool from a template: `sbt nativeInit cmake <libname>`

3. Implement core project: *just a regular scala project*

4. Generate native headers: `sbt javah`

5. Implement native headers in library: *project specific, see examples*

6. Build/run/test:

   - Library: `publish`
   - Application: `core/run`

7. Develop:

   - Re-run `javah` only if `@native` methods changed
   - Otherwise, go to 6

## Examples
The [plugins' unit tests](plugin/src/sbt-test/sbt-jni) offer some simple examples. They can be run individually through these steps:

1. Publish the macros library locally `sbt publishLocal`.
2. Change to the test's directory and run `sbt -Dplugin.version=<version>-SNAPSHOT`.
3. Follow the instructions in the `test` file (only enter the lines that start with ">" into sbt).

Real-world use-cases of sbt-jni include:
- [serial communication library for scala](https://jodersky.github.io/flow)

## Building
Both the macro library (`sbt-jni-macros`) and the sbt plugins (`sbt-jni`) are published. This project uses sbt-doge to allow cross-building on a per-project basis:

- sbt-jni-macros is built against Scala 2.11 and 2.12
- sbt-jni is built against Scala 2.10 (the Scala version that sbt 0.13 uses)

The differing Scala versions make it necessary to always cross-compile and cross-publish this project, i.e. append a "+" before every task.

Run `+publishLocal` to build and use this plugin locally.

## Copying
This project is released under the terms of the 3-clause BSD license. See LICENSE for details.
