package ch.jodersky.jni

import quoted.*

object nativeLoaderMacro:
  inline def load(nativeLibrary: String) = ${ nativeLoaderMacro.impl('nativeLibrary) }

  def impl(nativeLibrary: Expr[String])(using qctx: Quotes): Expr[Unit] =
    '{
      def loadPackaged(): Unit = {
        import ch.jodersky.jni.nativeLoaderMacro
        import java.nio.file.{Files, Path}

        val lib: String = System.mapLibraryName($nativeLibrary)

        val tmp: Path = Files.createTempDirectory("jni-")
        val plat: String = {
          val line =
            try {
              scala.sys.process.Process("uname -sm").lazyLines.head
            } catch {
              case ex: Exception => sys.error("Error running `uname` command")
            }
          val parts = line.split(" ")
          if (parts.length != 2) {
            sys.error("Could not determine platform: 'uname -sm' returned unexpected string: " + line)
          } else {
            val arch = parts(1).toLowerCase.replaceAll("\\s", "")
            val kernel = parts(0).toLowerCase.replaceAll("\\s", "")
            arch + "-" + kernel
          }
        }
        val resourcePath: String = "/native/" + plat + "/" + lib
        val resourceStream = Option(nativeLoaderMacro.getClass.getResourceAsStream(resourcePath)) match {
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
        System.loadLibrary($nativeLibrary)
      } catch {
        case ex: UnsatisfiedLinkError => loadPackaged()
      }
      load()
    }
