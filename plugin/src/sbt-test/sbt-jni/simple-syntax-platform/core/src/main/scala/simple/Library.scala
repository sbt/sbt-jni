package simple

import com.github.sbt.jni.syntax.NativeLoader

object Library extends NativeLoader("demo0") {

  @native def say(message: String): Int

}
