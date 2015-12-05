package ch.jodersky.sbt.jni
package build

import Keys._
import sbt._
import sbt.Keys._
import sbt.Logger
import java.io.File


object CMake extends BuildTool {

  val name = "CMake"

  def detect(baseDirectory: File) = baseDirectory.list().contains("CMakeLists.txt")

  object api extends BuildToolApi {

    def clean(baseDirectory: File, log: Logger) = Process("make clean", baseDirectory) ! log

    def library(
      baseDirectory: File,
      targetDirectory: File,
      log: Logger
    ): File = {
      val out = targetDirectory
      val outPath = out.getAbsolutePath

      val configure = Process(
        //Disable producing versioned library files, not needed for fat jars.
        s"cmake -DCMAKE_INSTALL_PREFIX:PATH=$outPath -DLIB_INSTALL_DIR:PATH=$outPath -DENABLE_VERSIONED_LIB:BOOLEAN=OFF",
        baseDirectory
      )

      val make = Process("make", baseDirectory)

      val makeInstall = Process("make install", baseDirectory)

      val ev = configure #&& make #&& makeInstall ! log
      if (ev != 0) sys.error(s"Building native library failed. Exit code: ${ev}")

      val products: List[File] = (out ** ("*" -- "*.la")).get.filter(_.isFile).toList

      //only one produced library is expected
      products match {
        case Nil =>
          sys.error("No files were created during compilation, " +
            "something went wrong with the autotools configuration.")
        case head :: Nil =>
          head
        case head :: tail =>
          log.warn("More than one file was created during compilation, " +
            s"only the first one (${head.getAbsolutePath}) will be used.")
          head
      }
    }
  }

}

