package ch.jodersky.jni

import java.io.IOException
import scala.sys.process.Process

/**
 * A platform is the representation of an architecture-kernel combination.
 * It is a somewhat ambigous concept defined as the set of all system configurations
 * capable of running a native binary.
 */
case class Platform private (arch: String, kernel: String) {

  /**
   * String representation of this platform. It is inspired by Autotools' platform
   * triplet, without the vendor field.
   */
  def id = arch + "-" + kernel

}

object Platform {

  /** The unknown platform. */
  final val Unknown = Platform("unknown", "unknown")

  /** Creates a platform with spaces stripped and case normalized. */
  def normalized(arch: String, kernel: String): Platform = {
    def normalize(str: String) = str.toLowerCase.filter(!_.isWhitespace)
    Platform(normalize(arch), normalize(kernel))
  }

  /** Runs 'uname' to determine current platform. Returns None if uname does not exist. */
  def uname: Option[Platform] = {
    val lineOpt = try {
      Some(Process("uname -sm").lines.head)
    } catch {
      case _: IOException => None
    }
    lineOpt.map { line =>
      val parts = line.split(" ")
      if (parts.length != 2) {
        sys.error("Could not determine platform: 'uname -sm' returned unexpected string: " + line)
      } else {
        Platform.normalized(parts(1), parts(0))
      }
    }
  }

  /** Determines platform the current JVM is running on. */
  def current = uname

  /** Parse an id to a platform. */
  def fromId(id: String) = {
    val (arch, dashKernel) = id.span(_ != '-')
    Platform(arch, dashKernel.drop(1))
  }

}
