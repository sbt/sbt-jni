package com.github.sbt.jni
package util

import scala.collection.IterableLike
import scala.collection.generic.CanBuildFrom
import scala.language.implicitConversions

class CollectionOps[A, Repr](xs: IterableLike[A, Repr]) {
  def distinctBy[B, That](f: A => B)(implicit cbf: CanBuildFrom[Repr, A, That]) = {
    val builder = cbf(xs.repr)
    val i = xs.iterator
    var set = Set[B]()
    while (i.hasNext) {
      val o = i.next
      val b = f(o)
      if (!set(b)) {
        set += b
        builder += o
      }
    }
    builder.result
  }
}

object CollectionOps {
  implicit def toCollectionOps[A, Repr](xs: IterableLike[A, Repr]): CollectionOps[A, Repr] = new CollectionOps(xs)
}
