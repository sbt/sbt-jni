package com.github.sbt.jni.syntax

import java.nio.file.{Files, Path}

class NativeLoader(nativeLibrary: String) {
  NativeLoader.load(nativeLibrary)
}

object NativeLoader {
  def load(nativeLibrary: String): Unit = {
    def loadPackaged(): Unit = {

      val lib: String = System.mapLibraryName(nativeLibrary)

      val tmp: Path = Files.createTempDirectory("jni-")
      val plat: String = {
        val (kernel, arch) = determinePlatform()
        arch + "-" + kernel
      }

      val resourcePath: String = "/native/" + plat + "/" + lib
      val resourceStream = Option(this.getClass.getResourceAsStream(resourcePath)) match {
        case Some(s) => s
        case None =>
          throw new UnsatisfiedLinkError(
            "Native library " + lib + " (" + resourcePath + ") cannot be found on the classpath."
          )
      }

      val extractedPath = tmp.resolve(lib)

      try {
        Files.copy(resourceStream, extractedPath)
      } catch {
        case ex: Exception => throw new UnsatisfiedLinkError("Error while extracting native library: " + ex)
      }

      System.load(extractedPath.toAbsolutePath.toString)
    }

    def load(): Unit = try {
      System.loadLibrary(nativeLibrary)
    } catch {
      case _: UnsatisfiedLinkError => loadPackaged()
    }

    def determinePlatform(): (String, String) = {
      val os = System.getProperty("os.name").toLowerCase match {
        case s if s.contains("win") => "windows"
        case s if s.contains("mac") => "darwin"
        case _                      => "linux"
      }

      val arch = System.getProperty("os.arch").toLowerCase match {
        case "arm64" | "aarch64" => "arm64"
        case _                   => "x86_64"
      }

      (os, arch)
    }

    load()
  }
}
