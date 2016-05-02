package simple

import ch.jodersky.jni.nativeLoader

@nativeLoader("demo0")
object Library {

  @native def say(message: String): Int

}
