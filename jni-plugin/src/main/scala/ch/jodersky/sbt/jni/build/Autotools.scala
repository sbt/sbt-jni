package ch.jodersky.sbt.jni
package build

import java.io.File
import sbt._

object Autotools extends BuildTool {

  val name = "Autotools"

  def detect(baseDirectory: File) = baseDirectory.list().contains("configure")

  object api extends ConfigureMakeInstall {

    override def configure(base: File, build: File, target: File) = {
      val targetPath = target.getAbsolutePath

      Process(
        s"${base.getAbsolutePath}/configure " +
          s"--prefix=$targetPath " +
          s"--libdir=$targetPath  " +
          "--disable-versioned-lib",
        build
      )
    }

  }

}

