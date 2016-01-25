# SBT-JNI

A suite of plugins for simplifying creation and distribution of JNI programs.

## Motivation
Java Native Interface (JNI), is a standard interface to allow programs written in a JVM language to interact with native code. Such programs can be divided into two logical parts: the JVM part, consisting of source files that will generate java bytecode (e.g. Scala or Java), and the native part, consisting of source files that will be compiled down to machine-native code (e.g. C, C++ or assembly). Interaction between the two parts is managed by header files generated through the `javah` utility.

Using native code can be beneficial in some situations, it can for example provide performance gains or otherwise infeasable features such as system-level interaction with peripherals. However, it also adds a few complexities, notably:

- Compilation: the project is divided into two parts, each of which require separate compilation. Furthermore, header generation with `javah` has to be dealt with.
- Portability: native binaries only run on the platform on which they were compiled.
- Distribution: native binaries must be made available and packaged for every supported platform.

The second point, portability, is inherent to JNI and thus unavoidable. However the first and last points can be greatly simplified with the help of build tools.

## Plugin Overview
This project consists of four autoplugins that aim to solve the difficulties in compiling and distributing JNI programs. These plugins provide two functionalities (compiling and distributing) for two parts of a project (JVM and native). The plugins are listed in the following table:

Functionality | JVM part        | Native part
--------------|-----------------|-------------
Compiling     | JniJavah        | JniNative
Packaging     | JniLoading      | JniPackaging

A project using this suite of plugins must be divided into two sub-projects, corresponding to the two parts: one that will contain JVM sources and one that will contain native sources.

The reason for dividing a project into two subprojects is two-fold: it enables flexible plugging of native sources and also integrates easily into the existing maven ecosystem. Adding the native binaries as additional artifacts has issues with scala versioning.

## Usage
Add plugin dependency. In `project/plugins.sbt`:
```scala
addSbtPlugin("ch.jodersky" % "sbt-jni" % "0.4.1")
```

Define sub-projects for JVM and native sources. In `myproject/build.sbt`:

```scala
lazy val core = project in file("myproject-core") // contains regular jvm sources and @native methods
lazy val native = project in file("myproject-native") // contains native sources
```

Select plugins to enable on sub-projects:

- In `myproject-core/build.sbt`:

    ```scala
    //enablePlugin(JniJavah) // this plugin is added to all JVM projects by default
    enablePlugin(JniLoading)
    ```

- In `myproject-native/build.sbt`:

    ```scala
    enablePlugin(JniNative)
    //enablePlugin(JniPackaging) // this plugin is added to all projects using JniNative by default
    ```

See an example [build.sbt](samples/basic/build.sbt).

Note that some plugins are added by default. To disable their functionality (for example if you don't wish to package native libraries), add `disablePlugin(<plugin>)` to the corresponding build definition.

## Plugin Details
The following gives a detailed description about the various JNI plugins and the ways to customize their behaviour. More details can be found in their [implementations](jni-plugin/src/main/scala/ch/jodersky/sbt/jni/plugins).

### JniJavah
*Add to sub-projects: JVM*

Adds `javah` task to generate header files for classes containing `@native` methods.

Customization: `target in javah := file("myproject-native") / "src" / "include"`, to point to `include` directory of native sources.

### JniNative
*Add to sub-projects: native*

Provides sbt tasks to call into a native build system such as Automake or CMake. Run `nativeCompile` to compile native sources.

Customization: `sourceDirectory in nativeCompile := file("src")`, to point to a directory containing the native build definition. Supported build tools are:

- Automake
- CMake

*Make sure the native build configurations respect the arguments and output directories as specified in the sample build configurations.*

### JniLoading
*Add to sub-projects: JVM*

Enables loading of libraries packaged with `JniPackaging` plugin.

### JniPackaging
*Add to sub-projects: native*

Packages native libraries in main jar, thus enabling easy publshing and distribution.

## Examples

See projects in the `samples` directory for some simple use-case examples. Note that in order to run these, sbt-jni will have to published locally first.

Another, more involved example is a [serial communication library](https://jodersky.github.io/flow).

## Copying
This project is released under the terms of the 3-clause BSD license. See LICENSE for details.
