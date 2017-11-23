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
 
  val Is64 = System.getProperty("os.arch").toUpperCase == "AMD64" 
}