package com.github.sbt.jni
package util

// On Scala 2.13+ / Scala 3 `distinctBy` is part of the standard collections,
// so this object only needs to exist to keep `import CollectionOps._` resolving.
object CollectionOps
