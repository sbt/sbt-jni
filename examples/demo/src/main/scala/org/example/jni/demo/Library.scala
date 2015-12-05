package org.example.jni.demo

/** A demo object, mapping to a native library. */
object Library {

  @native def print(message: String): Int

}
