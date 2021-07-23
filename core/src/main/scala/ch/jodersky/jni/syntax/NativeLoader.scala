package ch.jodersky.jni.syntax

import ch.jodersky.jni.nativeLoaderMacro

class NativeLoader(nativeLibrary: String) {
  nativeLoaderMacro.load(nativeLibrary)
}
