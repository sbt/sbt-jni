package ch.jodersky.jni

import java.io.{File, FileOutputStream, InputStream, OutputStream}

/**
 * Provides enhanced native library loading functionality.
 */
object NativeLoader {

  /** Name of the shared library file that is contained in a jar. */
  final val LibraryName = "library"

  final val BufferSize = 4096

  /** Extracts a resource from this class loader to a temporary file. */
  private def extract(path: String): Option[File] = {
    var in: Option[InputStream] = None
    var out: Option[OutputStream] = None

    try {
      in = Option(NativeLoader.getClass.getResourceAsStream(path))
      if (in.isEmpty) return None

      val file = File.createTempFile(path, "")
      out = Some(new FileOutputStream(file))

      val buffer = new Array[Byte](BufferSize)
      var length = -1;
      do {
        length = in.get.read(buffer)
        if (length != -1) out.get.write(buffer, 0, length)
      } while (length != -1)

      Some(file)
    } finally {
      in.foreach(_.close)
      out.foreach(_.close)
    }
  }

  private def loadError(msg: String): Nothing = throw new UnsatisfiedLinkError(
    "Error during native library extraction " +
      "(this can happen if your platform is not supported): " +
      msg
  )

  /**
   * Gets the absolute, full path of a resource on the classpath, given a libraryPath
   * and platform.
   */
  def fullLibraryPath(libraryPath: String, platform: Platform) = {
    libraryPath + "/native/" + platform.id + "/" + LibraryName
  }

  private def loadFromJar(libraryPath: String): Unit = {
    val platform = Platform.current.getOrElse {
      loadError("Cannot determine current platform.")
    }

    val resource = fullLibraryPath(libraryPath, platform)

    val file = extract(resource) getOrElse loadError(
      s"Shared library $resource not found."
    )
    System.load(file.getAbsolutePath())
  }

  /**
   * Loads a native library from the available library path or fall back
   * to extracting and loading a native library from available resources.
   */
  def load(libraryPath: String, library: String): Unit = try {
    System.loadLibrary(library)
  } catch {
    case ex: UnsatisfiedLinkError => loadFromJar(libraryPath)
  }

}
