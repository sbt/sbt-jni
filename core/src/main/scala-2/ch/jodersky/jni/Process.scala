package ch.jodersky.jni

object Process {
  def out(command: String): String =
    try {
      scala.sys.process.Process("uname -sm").lineStream.head
    } catch {
      case ex: Exception => sys.error("Error running `uname` command")
    }
}
