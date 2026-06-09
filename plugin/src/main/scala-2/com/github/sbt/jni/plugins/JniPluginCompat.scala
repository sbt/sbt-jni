package com.github.sbt.jni
package plugins

import sbt._
import xsbti.{FileConverter, VirtualFileRef}
import xsbti.compile.CompileAnalysis

import java.io.File

/**
 * sbt 1.x (Scala 2.12) flavour of the cross-version compatibility helpers.
 *
 * On sbt 1.x classpath entries are plain `File`s and the file converter is a no-op here.
 */
private[jni] object JniPluginCompat {

  def toFile(entry: Attributed[File], converter: FileConverter): File =
    entry.data

  def productKeys(analysis: CompileAnalysis): scala.collection.Set[VirtualFileRef] = {
    import scala.collection.JavaConverters._
    analysis.readStamps().getAllProductStamps().asScala.keySet
  }

  // sbt 2.x introduces `Def.uncached` to opt a task out of the build cache.
  // On sbt 1.x there is no build cache, so this is a transparent no-op.
  implicit class DefOps(private val self: Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }
}
