package ch.jodersky.jni.basic

import ch.jodersky.jni.NativeLoader

object Main {

  def main(args: Array[String]): Unit = {
    NativeLoader.load("/ch/jodersky/jni/basic", "demo1")
    val r = Library.print("Hello world!\n")
    println("Returned: " + r)
  }

}
