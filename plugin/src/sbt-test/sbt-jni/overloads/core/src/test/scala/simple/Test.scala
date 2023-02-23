package simple

import org.scalatest.flatspec._

class SimpleSpec extends AnyFlatSpec {

  "Calling native methods in tests" should "work" in {
    assert(Library.say("hello") == 42)
    assert(Library.say("hello", 1) == 43)
    assert(Library.say("hello", 1, 2L) == 44)
    assert(Library.say(Array("hello world1", "hello world2")) == 45)
  }

}
