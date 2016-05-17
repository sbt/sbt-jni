package simple

import org.scalatest._

class SimpleSpec extends FlatSpec {

  "Calling native methods in tests" should "work" in {
    assert(Library.say("hello") == 42)
  }

}
