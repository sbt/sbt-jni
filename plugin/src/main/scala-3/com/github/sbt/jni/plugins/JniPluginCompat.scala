package com.github.sbt.jni
package plugins

import sbt.*
import xsbti.{FileConverter, HashedVirtualFileRef, VirtualFileRef}
import xsbti.compile.CompileAnalysis

import java.io.File

private[jni] object JniPluginCompat {

  def toFile(entry: Attributed[HashedVirtualFileRef], converter: FileConverter): File =
    converter.toPath(entry.data).toFile

  def productKeys(analysis: CompileAnalysis): scala.collection.Set[VirtualFileRef] = {
    import scala.jdk.CollectionConverters.*
    analysis.readStamps().getAllProductStamps().asScala.keySet
  }
}
