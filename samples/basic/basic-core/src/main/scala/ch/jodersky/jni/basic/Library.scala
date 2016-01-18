package ch.jodersky.jni.basic

/** A demo object, mapping to a native library. */
object Library {

  @native def print(message: String): Int

}
