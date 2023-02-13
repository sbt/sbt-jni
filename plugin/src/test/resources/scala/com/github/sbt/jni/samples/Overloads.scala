package com.github.sbt.jni.samples

private[samples] case class A(x: Int)
private[samples] case class B(x: Double)
private[samples] case class C(x: Long)

class Overloads {
  @native
  def doSomething(param: A): B
  @native
  def doSomething(param: B): A
  @native
  def doSomething(param: Array[C]): A
  @native
  def doSomething(parma0: Int, param1: Array[C], param2:B, param3: Double): A
}
