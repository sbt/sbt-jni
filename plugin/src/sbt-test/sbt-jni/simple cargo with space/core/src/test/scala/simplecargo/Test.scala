package simplecargo

import org.scalatest.flatspec._

class SimpleSpec extends AnyFlatSpec {

  "Calling native methods in tests" should "work" in {
    assert(new Adder(12).plus(34) == 46)
  }

}
