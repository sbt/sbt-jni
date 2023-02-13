package com.github.sbt.jni.javah

import com.github.sbt.jni.javah.util.ClassMetaInfo
import org.objectweb.asm.ClassReader
import org.scalatest.funspec.AnyFunSpec
import scala.jdk.CollectionConverters.asScalaBuffer

import java.io.File
import java.nio.file.Files

class NativeMethodTests extends AnyFunSpec {
  this.
    describe("NativeMethods") {
      describe("when a method is overloaded") {
        it("should name methods correctly") {
          val expectedNames = Set("doSomething__Lcom_github_sbt_jni_samples_A_2",
            "doSomething__Lcom_github_sbt_jni_samples_B_2",
            "doSomething___3Lcom_github_sbt_jni_samples_C_2",
            "doSomething__I_3Lcom_github_sbt_jni_samples_C_2Lcom_github_sbt_jni_samples_B_2D")
          val methods = asScalaBuffer(NativeMethodTests.classMeta.methods)
          for (m <- methods) {
            assert(expectedNames.contains(m.longMangledName()))
          }
        }
      }
    }
}

object NativeMethodTests {
  lazy val classMeta: ClassMetaInfo = {
    val resource = getClass.getClassLoader.getResource("classes/com/github/sbt/jni/samples/Overloads.class").getPath
    val f = new File(resource)
    val meta = new ClassMetaInfo()
    val stream = Files.newInputStream(f.toPath)
    try {
      val reader = new ClassReader(stream)
      reader.accept(meta, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES)
    }
    finally {
      stream.close()
    }
    meta
  }
}
