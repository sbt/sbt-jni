package simple

import com.github.sbt.jni.nativeLoader

@nativeLoader("demo0")
object Library {

  @native def say(message: String): Int
  @native def say(message: String, i: Int): Int
  @native def say(message: String, i: Int, l: Long): Int

}
