package simple

object Main {

  def main(args: Array[String]): Unit = {
    val result1 = Library.say("hello world")
    val result2 = Library.say("hello world", 1)
    val result3 = Library.say("hello world", 1, 2L)
    val result4 = Library.say(Array("hello world1", "hello world2"))
    
    assert(result1 == 42)
    assert(result2 == 43)
    assert(result3 == 44)
    assert(result4 == 45)
  }

}
