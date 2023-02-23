package com.github.sbt.jni.javah

import com.github.sbt.jni.javah.util.ClassMetaInfo
import org.objectweb.asm.ClassReader
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers
import scala.jdk.CollectionConverters._

import java.io.File
import java.nio.file.Files

class NativeMethodTests extends AnyWordSpec with Matchers {
  "NativeMethods" when {
    "a method is overloaded" should {
      "name methods correctly" in {
        val expectedNames = Set(
          "doSomething__Lcom_github_sbt_jni_javah_A_2",
          "doSomething__Lcom_github_sbt_jni_javah_B_2",
          "doSomething___3Lcom_github_sbt_jni_javah_C_2",
          "doSomething__I_3Lcom_github_sbt_jni_javah_C_2Lcom_github_sbt_jni_javah_B_2D"
        )
        val methods = NativeMethodTests.classMeta.methods.asScala
        methods.foreach { m => expectedNames must contain(m.longMangledName()) }
      }
    }
  }
}

object NativeMethodTests { self =>
  lazy val classMeta: ClassMetaInfo = {
    val resource = self.getClass.getResource("Overloads.class").getPath
    val meta = new ClassMetaInfo()
    val stream = Files.newInputStream(new File(resource).toPath)
    try {
      val reader = new ClassReader(stream)
      reader.accept(meta, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES)
    } finally stream.close()
    meta
  }
}
