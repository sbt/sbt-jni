package multiclasses

import ch.jodersky.jni.nativeLoader

@nativeLoader("multiplier1")
abstract class Multiplier {

  def base: Int = 1

  @native def times(factor: Int): Int

}
