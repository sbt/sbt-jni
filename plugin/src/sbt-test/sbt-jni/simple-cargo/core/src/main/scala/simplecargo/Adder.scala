package simplecargo

import com.github.sbt.jni.nativeLoader

@nativeLoader("adder")
class Adder(val base: Int) {
  @native def plus(term: Int): Int // implemented in libadder.so
}
