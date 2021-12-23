package simplecargo

import com.github.sbt.jni.syntax.NativeLoader

class Adder(val base: Int) extends NativeLoader("adder") {
  @native def plus(term: Int): Int // implemented in libadder.so
}
