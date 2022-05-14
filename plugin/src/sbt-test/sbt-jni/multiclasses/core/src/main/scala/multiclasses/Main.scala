package multiclasses

object Main {

  def addition(): Unit = {
    val zero = new Adder(0)
    val one = new Adder(1)
    assert(zero.plus(1) == 1)
    assert(one.plus(1) == 2)
    assert(Adder.sum(0, 1) == 1)

    assert(one.plusValue(Value(1)) == 2)
  }

  def multiplication(): Unit = {
    val zero = new Multiplier {
      override def base = 0
    }

    val one = new Multiplier {
      override def base = 1
    }
    assert(zero.times(1) == 0)
    assert(one.times(1) == 1)
  }

  def main(args: Array[String]): Unit = {
    addition()
    multiplication()
  }

}
