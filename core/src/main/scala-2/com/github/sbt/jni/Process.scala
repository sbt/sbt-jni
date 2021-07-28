package com.github.sbt.jni

object Process {
  def out(command: String): String =
    try {
      scala.sys.process.Process("uname -sm").!!.linesIterator.next()
    } catch {
      case _: Exception => sys.error("Error running `uname` command")
    }
}
