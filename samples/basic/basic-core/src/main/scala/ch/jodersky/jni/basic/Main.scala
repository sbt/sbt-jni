package ch.jodersky.jni.basic

import ch.jodersky.jni.NativeLoader

object Main {

  def main(args: Array[String]): Unit = {
    NativeLoader.load("/ch/jodersky/jni/basic/native", "demo1")
    val result: Int = Library.print("Hello world!\n")
    println("Returned: " + result)
  }

}
