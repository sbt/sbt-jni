package ch.jodersky.jni

import macrocompat.bundle

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly

@bundle
class nativeLoaderMacro(val c: Context) {

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val nativeLibrary: String = c.prefix.tree match {
      case Apply(_, List(Literal(Constant(x: String)))) => x
      case Apply(_, xs :: tail) => c.abort(xs.pos, "Native library must be a constant.")
      case t => c.abort(t.pos, "Native library not specified.")
    }

    def inject(annottees: List[Tree]): List[Tree] = annottees match {

      case ClassDef(mods, name, tparams, Template(parents, self, body)) :: tail =>
        val extra = q"""
        {
          ${name.toTermName}
        }
        """

        val module: List[Tree] = tail match {
          case Nil => inject(List(q"""object ${name.toTermName}"""))
          case other => inject(other)
        }

        ClassDef(mods, name, tparams, Template(parents, self, body :+ extra)) :: module

      //q"$mods object $name extends ..$parents {$self => ..$body }" :: Nil =>
      case ModuleDef(mods, name, Template(parents, self, body)) :: Nil =>
        val extra = q"""
          {
            def loadPackaged(): Unit = {
              import java.io.File
              import java.nio.file.{Files, Path}

              val lib: String = System.mapLibraryName($nativeLibrary)

              val tmp: Path = Files.createTempDirectory("jni-")
              val plat: String = {
                val osName = ch.jodersky.sbt.jni.util.OsAndArch.OsName
                val osArch = ch.jodersky.sbt.jni.util.OsAndArch.OsArch
                osName + "-" + osArch
              }

              val resourcePath: String = "/native/" + plat + "/" + lib
              val resourceStream = Option($name.getClass.getResourceAsStream(resourcePath)) match {
                case Some(s) => s
                case None => throw new UnsatisfiedLinkError(
                  "Native library " + lib + " (" + resourcePath + ") cannot be found on the classpath.")
              }

              val extractedPath = tmp.resolve(lib)

              try {
                Files.copy(resourceStream, extractedPath)
              } catch {
                case ex: Exception => throw new UnsatisfiedLinkError(
                  "Error while extracting native library: " + ex)
              }

              System.load(extractedPath.toAbsolutePath.toString)
            }

            def load(): Unit = try {
              System.loadLibrary($nativeLibrary)
            } catch {
              case ex: UnsatisfiedLinkError => loadPackaged()
            }

            load()
          }
          """

        ModuleDef(mods, name, Template(parents, self, body :+ extra)) :: Nil

      case _ =>
        c.abort(c.enclosingPosition, "nativeLoader can only be annotated to classes and singleton objects")

    }

    val outputs = inject(annottees.map(_.tree).toList)

    val result: Tree = Block(outputs, Literal(Constant(())))

    c.Expr[Any](result)
  }

}

@compileTimeOnly("Macro Paradise must be enabled to apply annotation.")
class nativeLoader(nativeLibrary: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro nativeLoaderMacro.impl
}
