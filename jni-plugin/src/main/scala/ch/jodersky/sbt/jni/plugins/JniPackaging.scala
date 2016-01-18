package ch.jodersky.sbt.jni
package plugins

import ch.jodersky.jni._
import sbt._
import sbt.Keys._

/** Packages libraries built with JniNative. */
object JniPackaging extends AutoPlugin {

  //JvmPlugin is required or else resource generators will be overriden
  override def requires = JniNative && plugins.JvmPlugin
  override def trigger = allRequirements

  object autoImport {

    val nativeLibraryPath = settingKey[String](
      "String that is prepended to the path of a native library when packaged. This value should be " +
        "passed to `NativeLoader.load()`"
    )

    val enableNativeCompilation = settingKey[Boolean](
      "Determines if native compilation is enabled. If disabled, only pre-compiled libraries in " +
        "`unmanagedNativeDirectories` will be packaged."
    )

    val unmanagedNativeDirectories = settingKey[Seq[File]](
      "Unmanaged directories containing native libraries. The libraries must be regular files " +
        "contained in a subdirectory corresponding to a platform. For example " +
        "`<unamagedNativeDirectory>/x86_64-linux/libfoo.so` is an unmanaged library for machines having " +
        "the x86_64 architecture and running the Linux kernel."
    )

    val unmanagedNativeLibraries = taskKey[Map[Platform, File]](
      "Reads `unmanagedNativeDirectories` and maps platforms to library files specified theirin."
    )

    val managedNativeLibraries = taskKey[Map[Platform, File]](
      "Maps locally built, platform-dependant libraries."
    )

  }
  import autoImport._
  import JniNative.autoImport._

  lazy val settings: Seq[Setting[_]] = Seq(

    nativeLibraryPath := {
      val orgPath = organization.value.replaceAll("\\.", "/")
      s"/${orgPath}/${name.value}/native"
    },

    enableNativeCompilation := true,

    unmanagedNativeDirectories := Seq(baseDirectory.value / "lib_native"),

    unmanagedNativeLibraries := {
      val dirs: Seq[File] = unmanagedNativeDirectories.value
      val seq: Seq[(Platform, File)] = for (
        dir <- dirs;
        if (dir.exists && dir.isDirectory);
        platformDir <- dir.listFiles();
        if platformDir.isDirectory;
        platform = Platform.fromId(platformDir.name);
        library <- platformDir.listFiles();
        if library.isFile
      ) yield {
        platform -> library.getCanonicalFile()
      }

      seq.toMap
    },

    managedNativeLibraries := Def.taskDyn[Map[Platform, File]] {
      val enableManaged = (enableNativeCompilation).value
      if (enableManaged) Def.task {
        val library: File = nativeCompile.value
        val platform = nativePlatform.value
        Map(platform -> library)
      }
      else Def.task {
        Map()
      }
    }.value,

    resourceGenerators += Def.task {

      val libraries: Seq[(Platform, File)] = (managedNativeLibraries.value ++ unmanagedNativeLibraries.value).toSeq

      val resources: Seq[File] = for ((plat, file) <- libraries) yield {

        //native library as a managed resource file
        val resource = resourceManaged.value /
          NativeLoader.fullLibraryPath(
            (nativeLibraryPath).value,
            plat
          )

        //copy native library to a managed resource (so it can also be loaded when not packaged as a jar)
        IO.copyFile(file, resource)
        resource
      }
      resources
    }.taskValue,

    //don't add scala version to native jars
    crossPaths in Global := false

  )

  override lazy val projectSettings = inConfig(Compile)(settings)

}
