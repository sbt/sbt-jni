package ch.jodersky.sbt.jni
package build

import java.io.File
import sbt._

object CMake extends BuildTool {

  val name = "CMake"

  def detect(baseDirectory: File) = baseDirectory.list().contains("CMakeLists.txt")

  object api extends ConfigureMakeInstall {

    override def configure(base: File, build: File, target: File) = {
      val targetPath = target.getAbsolutePath
      Process(
        //Disable producing versioned library files, not needed for fat jars.
        "cmake " +
          s"-DCMAKE_INSTALL_PREFIX:PATH=$targetPath " +
          s"-DLIB_INSTALL_DIR:PATH=$targetPath " +
          "-DENABLE_VERSIONED_LIB:BOOLEAN=OFF " +
          base.getAbsolutePath,
        build
      )
    }

  }

}

