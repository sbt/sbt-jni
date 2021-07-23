package simple

import ch.jodersky.jni.syntax.NativeLoader

object Library extends NativeLoader("demo0") {

  @native def say(message: String): Int

}
