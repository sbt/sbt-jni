package com.github.sbt.jni.javah

private[javah] case class A(x: Int)
private[javah] case class B(x: Double)
private[javah] case class C(x: Long)

class Overloads {
  @native
  def doSomething(param: A): B
  @native
  def doSomething(param: B): A
  @native
  def doSomething(param: Array[C]): A
  @native
  def doSomething(parma0: Int, param1: Array[C], param2: B, param3: Double): A
}
