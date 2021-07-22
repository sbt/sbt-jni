package ch.jodersky.jni

import scala.reflect.macros.whitebox.Context

object nativeLoaderMacro {
  def impl(c: Context)(nativeLibrary: c.Expr[String], clz: c.Expr[Class[_]]): c.Expr[Unit] = {
    import c.universe._
    c.Expr(q"""
      {
        def loadPackaged(): Unit = {
          import java.nio.file.{Files, Path}

          val lib: String = System.mapLibraryName($nativeLibrary)

          val tmp: Path = Files.createTempDirectory("jni-")
          val plat: String = {
            val line = try {
              scala.sys.process.Process("uname -sm").lineStream.head
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
          val resourceStream = Option($clz.getResourceAsStream(resourcePath)) match {
            case Some(s) => s
            case None => throw new UnsatisfiedLinkError("Native library " + lib + " (" + resourcePath + ") cannot be found on the classpath.")
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
      }""")
  }
}
