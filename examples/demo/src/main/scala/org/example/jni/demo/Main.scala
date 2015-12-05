package org.example.jni.demo

import ch.jodersky.jni.NativeLoader

object Main {

  def main(args: Array[String]): Unit = {
    NativeLoader.load("demo1", "/org/example/jni/demo")
    Library.print("Hello world!")
  }

}
