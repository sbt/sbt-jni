[Work in progress]

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
This project consists of 4 autoplugins that aim to solve the difficulties in compiling and distributing JNI programs. These plugins provide two functionalities (compiling and distributing) for two parts of a project (JVM and native):

Plugins
--------------|-----------------|-------------
Functionality | JVM part        | Native part
--------------|-----------------|-------------
Compiling     | JniJavah        | JniNative
Packaging     | JniLoading      | JniPackaging

A project using this suite of plugins must be divided into two sub-projects, corresponding to the two parts: one that will contain JVM sources and one that will contain native sources.

The reason for dividing a project into two subprojects is two-fold: it enables flexible plugging of native sources and also integrates easily into the existing maven ecosystem. Adding the native binaries as additional artifacts has issues with scala versioning.

## Usage
Add plugin dependency. In `project/plugins.sbt`:
```scala
addSbtPlugin("ch.jodersky" % "sbt-jni" % "0.3")
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

Note that some plugins are added by default. To disable their functionality (for example if you don't wish to package native libraries), add `disablePlugin(<plugin>)` to the corresponding build definition.


## Plugin Details
The following gives a detailed description about the various JNI plugins and the ways to customize their behaviour.

### Javah
*JVM sub-projects*

#### Functionality
Adds a `javah` task to generate header files for classes containing `@native` methods.

#### Customization
Change target to point to `include` directory of native sources: `target in javah := file("myproject-native") / "src" / "include"`


### JniNative
*Native sub-projects*

#### Functionality
Provides sbt tasks to call into a native build system such as Automake or CMake. Run `sbt nativeCompile` to compile native sources.

#### Customization
Change source directory to point to a directory containing the native build definition. Supported build tools are:

- Automake
- CMake

*Make sure the native build configurations respect the arguments and output directories as specified in the [native build templates](templates)*

### JniLoading
*JVM sub-projects*

### JniPackaging
*Native sub-projects*

## Examples
