package com.github.sbt.jni

package object build {
  implicit class StringOps(val self: String) extends AnyVal {
    def enquoted: String = s""""$self""""
  }
}
