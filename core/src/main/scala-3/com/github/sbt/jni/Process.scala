package com.github.sbt.jni

object Process {
  def out(command: String): String =
    try {
      scala.sys.process.Process("uname -sm").lazyLines.head
    } catch {
      case ex: Exception => sys.error("Error running `uname` command")
    }
}
