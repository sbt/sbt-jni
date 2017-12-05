package ch.jodersky.sbt.jni.util

object OsAndArch {

  val WindowsName = "windows"
  val MacName = "mac"
  val LinuxName = "linux"
  val UnknownName = "unknown"

  val OsName = {
    val raw = System.getProperty("os.name").toLowerCase
    if(raw.indexOf("win") >= 0) WindowsName
    else if(raw.indexOf("mac") >= 0) MacName
    else if(raw.indexOf("nux") >= 0) LinuxName
    else UnknownName
  }

  // Windows requires special treatment
  val IsWindows = OsName == WindowsName

  val OsArch = if(IsWindows) {
    System.getProperty("os.arch")
  } else {
    val line = try {
      scala.sys.process.Process("uname -sm").lines.head
    } catch {
      case ex: Exception => sys.error("Error running `uname` command")
    }
    val parts = line.split(" ")
    if (parts.length != 2) {
      sys.error("Could not determine platform: 'uname -sm' returned unexpected string: " + line)
    }else {
      parts(1).toLowerCase.replaceAll("\\s", "")
    }
  }
}