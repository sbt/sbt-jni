package multiclasses

import ch.jodersky.jni.nativeLoader

@nativeLoader("demo0")
class Adder(base0: Int) {

  final private val base = base0
  
  @native def plus(term: Int): Int

}

object Adder {

  @native def sum(term1: Int, term2: Int): Int

}
