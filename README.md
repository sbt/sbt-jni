[![CI](https://github.com/sbt/sbt-jni/workflows/CI/badge.svg)](https://github.com/sbt/sbt-jni/actions) [![Maven Central](https://img.shields.io/maven-central/v/com.github.sbt/sbt-jni-core_3)](https://search.maven.org/search?q=g:com.github.sbt%20AND%20a:sbt-jni) [![Snapshots](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.github.sbt/sbt-jni-core_3.svg)](https://oss.sonatype.org/content/repositories/snapshots/com/github/sbt/sbt-jni_2.12_1.0/)

| sbt version | group id | plugin version |
|-------------|----------------|----------------|
| 0.13.x      | ch.jodersky    | [1.2.6](https://scala.jfrog.io/artifactory/sbt-plugin-releases/ch.jodersky/sbt-jni/scala_2.10/sbt_0.13/1.2.6/) |
| 1.x         | ch.jodersky    | [1.4.1](https://scala.jfrog.io/artifactory/sbt-plugin-releases/ch.jodersky/sbt-jni/scala_2.12/sbt_1.0/1.4.1/) |
| 1.x         | com.github.sbt | [![Maven Central](https://img.shields.io/maven-central/v/com.github.sbt/sbt-jni-core_3)](https://search.maven.org/search?q=g:com.github.sbt%20AND%20a:sbt-jni) |

# SBT-JNI

A suite of sbt plugins for simplifying creation and distribution of JNI programs.

## Setup

Add sbt-jni as a dependency to `project/plugins.sbt`:
```scala
addSbtPlugin("com.github.sbt" % "sbt-jni" % "<latest version>")
```

where `<latest version>` refers to the version indicated by the badge above.

*Note: We changed the organization from `ch.jodersky` to `com.github.sbt`*

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
| JniPackage | Packages native libraries into multi-platform fat jars. No more manual library installation!     |

Note that most plugins are enabled in projects by default. Disabling their functionality can be achieved by adding `disablePlugins(<plugin>)` to the corresponding project definition (for example, should you wish to disable packaging of native libraries).

## Plugin Details

### JniJavah

| Enabled                        | Source        |
|--------------------------------|---------------|
| automatic, for all projects    | [JniJavah.scala](plugin/src/main/scala/com/github/sbt/jni/plugins/JniJavah.scala)|

This plugin wraps the JDK `javah` command [^1].

[^1]: Glavo's [gjavah](https://github.com/Glavo/gjavah) is actually used, since `javah` has been
removed from the JDK since version 1.10. If something goes wrong, please open an issue to help us improve it.

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
javah / target := <dir> // defaults to target/native/include
```

Note that native methods declared both in Scala and Java are supported. Whereas Scala uses the `@native` annotation, Java uses the
`native` keyword.

### JniLoad
| Enabled                        | Source        |
|--------------------------------|---------------|
| automatic, for all projects    | [JniLoad.scala](plugin/src/main/scala/com/github/sbt/jni/plugins/JniLoad.scala) |

This plugin enables loading native libraries in a safe and transparent manner to the developer (no more explicit, static `System.load("library")` calls required). It does so by providing a class annotation which injects native loading code to all its annottees. Furthermore, in case a native library is not available on the current `java.library.path`, the code injected by the annotation will fall back to loading native libraries packaged according to the rules of `JniPackage`.

#### Usage example (Scala 2.x):
```scala
import com.github.sbt.jni.nativeLoader

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

Note: this plugin is just a shorthand for adding `sbt-jni-core` (the project in `core/`) and the scala-macros-paradise (on Scala <= 2.13) projects as provided dependencies.

See the [annotation's implementation](core/src/main/scala/com/github/sbt/jni/annotations.scala) for details about the injected code.

#### Usage example (Scala 3.x / Scala 2.x):

Scala 3 has no macro annotations support. As a solution we don't need this to be a macro function anymore. As the result, this option requires to have an explicit dependency on the [sbt-jni-core](./core) library.

Note that if you want to run or test the project from sbt and have `ThisBuild / turbo := true`, you have to change the `classLoaderLayeringStrategy` to `ClassLoaderLayeringStrategy.Flat`, otherwise you will get `UnsatisfiedLinkError`, like `java.lang.UnsatisfiedLinkError: 'int simple.Adder.plus(int)'`.

This plugin behavior is configurable via:

```sbt
// set to `Provided` by default, `Compile` is needed to use syntax (`extends NativeLoader`)
sbtJniCoreScope := Compile
```

```scala
// to make the code below work the core project should be included as a dependency via
// sbtJniCoreScope := Compile
import com.github.sbt.jni.syntax.NativeLoader

// By adding this annotation, there is no need to call
// System.load("adder0") before accessing native methods.
class Adder(val base: Int) extends NativeLoader("adder0"):
  @native def plus(term: Int): Int // implemented in libadder0.so

// The application feels like a pure Scala app.
@main def main: Unit = (new Adder(0)).plus(1)
```

### JniNative
| Enabled                        | Source        |
|--------------------------------|---------------|
| manual                         | [JniNative.scala](plugin/src/main/scala/com/github/sbt/jni/plugins/JniNative.scala) |

JniNative adds the capability of building native code (compiling and linking) to sbt, by interfacing with commonly used build tools.

Since this plugin is basically a command-line wrapper, native build tools must follow certain calling conventions to be compatible. The supported build tools are currently:

- [CMake](#cmake)
- [Cargo (Rust)](#cargo)

An initial, compatible build template can be obtained by running `sbt nativeInit <tool>`. Once the native build tool initialised, projects are built by calling the `sbt nativeCompile` task.

Source and output directories are configurable
```scala
nativeCompile / sourceDirectory := sourceDirectory.value / "native"
nativeCompile / target := target.value / "native" / nativePlatform.value
```

#### CMake

A regular `CMake` native project definition usually looks this following way:

```scala
lazy val native = project
  // sourceDirectory = <project_root>/native/src
  .settings(nativeCompile / sourceDirectory := sourceDirectory.value)
  .enablePlugins(JniNative)
```

Source directory is set to `sourceDirectory.value` since the `CMake` project structure is of the following shape:

```
├── src/
│   ├── CMakeLists.txt
│   ├── lib.cpp
```

#### Cargo

A regular `Cargo` native project definition usually looks this following way:

```scala
lazy val native = project
  // baseDirectory = <project_root>/native
  .settings(nativeCompile / sourceDirectory := baseDirectory.value)
  .enablePlugins(JniNative)
```

Source directory is set to `baseDirectory.value` since the `Cargo` project structure is of the following shape:

```
├── Cargo.toml
├── src/
│   ├── lib.rs

```

By default, `Cargo` build is launched with the `--release` flag. It is possible to configure `Cargo` profile by overriding the `nativeBuildTool` setting:

```scala
nativeBuildTool := Cargo.make(release = false)
```

### JniPackage
| Enabled                        | Source        |
|--------------------------------|---------------|
| automatic, when JniNative enabled | [JniPackage.scala](plugin/src/main/scala/com/github/sbt/jni/plugins/JniPackage.scala) |

This plugin packages native libraries produced by JniNative in a way that they can be transparently loaded with JniLoad. It uses the notion of a native "platform", defined as the architecture-kernel values returned by `uname -sm`. A native binary of a given platform is assumed to be executable on any machines of the same platform.

## Canonical Use

*Keep in mind that sbt-jni is a __suite__ of plugins, there are many other use cases. This is a just a description of the most common one.*

1. Define separate sub-projects for JVM and native sources. In `myproject/build.sbt`:

   ```scala
   lazy val core = (project in file("myproject-core")) // regular scala code with @native methods
     .dependsOn(native % Runtime) // remove this if `core` is a library, leave choice to end-user

   lazy val native = (project in file("myproject-native")) // native code and build script
     .enablePlugins(JniNative) // JniNative needs to be explicitly enabled
   ```
   Note that separate projects are not strictly required. They are strongly recommended nevertheless, as a portability-convenience tradeoff: programs written in a JVM language are expected to run anywhere without recompilation, but including native libraries in jars limits this portability to only platforms of the packaged libraries. Having a separate native project enables the users to easily swap out the native library with their own implementation.

2. Initialize the native build tool from a template:

   Run `sbt "nativeInit cmake <libname>"`

3. Implement core project:

   This step is identical to building a regular scala project, with the addition that some classes will also contain `@native` methods.

4. Generate native headers:

   Run `sbt javah`

5. Implement native headers:

   The function prototypes in the header files must be implemented in native code (such as C, C++) and built into a shared library. Run `sbt nativeCompile` to call the native build tool and build a shared library.

6. Build/run/test:

   At this point, the project can be tested and run as any standard sbt project. For example, you can publish your project as a library (`sbt publish`), run it (`sbt core/run`) or simply run unit tests (`sbt test`). Packaging and recompiling of the native library will happen transparently.

7. Develop:

   The usual iterative development process. Nothing speial needs to be done, except in case any `@native` methods are added/removed or their signature changed, then `sbt javah` needs to be run again.

## Examples
The [plugins' unit tests](plugin/src/sbt-test/sbt-jni) offer some simple examples. They can be run individually through these steps:

1. Publish the core library locally `sbt publishLocal`.
2. Change to the test's directory and run `sbt -Dplugin.version=<version>`.
3. Follow the instructions in the `test` file (only enter the lines that start with ">" into sbt).

Real-world use-cases of sbt-jni include:

- [serial communication library for scala](https://github.com/jodersky/akka-serial)
- [PDAL Java bindings](https://github.com/PDAL/java)

## Requirements and Dependencies

- projects using `JniLoad` must use Scala versions 2.11, 2.12 or 2.13
- projects using `JniLoad` with Scala 3 should use it with 
- only POSIX platforms are supported (actually, any platform that has the `uname` command available)

The goal of sbt-jni is to be the least intrusive possible. No transitive dependencies are added to projects using any plugin (some dependencies are added to the `provided` configuration, however these do not affect any downstream projects).

## Building
Both the core (former macros) library (`sbt-jni-core`) and the sbt plugins (`sbt-jni`) are published. Cross-building happens on a per-project basis:

- sbt-jni-core is built against Scala 2.11, 2.12 and 2.13
- sbt-jni is built against Scala 2.12 (the Scala version that sbt 1.x uses)

The differing Scala versions make it necessary to always cross-compile and cross-publish this project, i.e. append a "+" before every task.

Run `sbt +publishLocal` to build and use this plugin locally.

## Copying
This project is released under the terms of the 3-clause BSD license. See [LICENSE](./LICENSE) for details.

`javah` is released under the terms of the MIT license since it uses Glavo's [gjavah](https://github.com/Glavo/gjavah). See [LICENSE](./plugin/src/main/java/com/github/sbt/jni/javah/LICENSE) for details.
