package ch.jodersky.jni
package util

import scala.language.experimental.macros

import scala.reflect.macros.whitebox.Context

object PlatformMacros {

  // arch-kernel
  def current(c: Context): c.Expr[String] = {
    import c.universe._
    val result = q"""
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
      """
    c.Expr[String](result)
  }

}
