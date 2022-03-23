package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse
import scala.collection.mutable

object ExposedTuples extends WartTraverser {
  def message: String =
    "Avoid using tuples in public interfaces, as they only supply type information. Consider using a custom case class to add semantic meaning."

  private final case class LineInFile(content: Option[String], startLine: Int)

  def apply(u: WartUniverse): u.Traverser = {
    val linesWithError = mutable.Set.empty[LineInFile]

    new u.Traverser(this) {
      import q.reflect.*
      def allTypes(t: TypeRepr): List[TypeRepr] = {
        t match {
          case AppliedType(_, args) =>
            t :: args.flatMap(allTypes)
          case _ =>
            t :: Nil
        }
      }

      def containsTuple(t: Seq[TypeRepr]): Boolean =
        t.flatMap(allTypes).exists(_.isTupleN)

      def addError(pos: Position): Unit = {
        error(pos, message)
        linesWithError.add(LineInFile(pos.sourceFile.content, pos.startLine))
      }

      def errorAlreadyExists(pos: Position): Boolean = {
        linesWithError.contains(LineInFile(pos.sourceFile.content, pos.startLine))
      }

      def implicitClassHasWartAnnotation(owner: Symbol, name: String): Boolean = {
        owner.declaredTypes.collect { case c if c.isClassDef => c.tree }.collect {
          case c: ClassDef if c.name == name => c
        }.exists(hasWartAnnotation)
      }

      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case _ if errorAlreadyExists(tree.pos) =>
          case t: DefDef =>
            if (
              !t.symbol.flags.is(Flags.Private) && t.name != "unapply" && !t.symbol.flags.is(
                Flags.Local
              ) && !implicitClassHasWartAnnotation(owner, t.name)
            ) {
              if (containsTuple(t.returnTpt.tpe :: Nil)) {
                addError(t.returnTpt.pos)
              } else if (containsTuple(t.termParamss.flatMap(_.params).map(_.tpt.tpe))) {
                addError(t.pos)
              }
            }
          case t: ValDef =>
            if (
              !t.symbol.flags.is(Flags.Private) && !owner.flags.is(Flags.Local) && containsTuple(
                t.tpt.tpe :: Nil
              ) && !t.symbol.flags.is(Flags.Synthetic)
            ) {
              addError(tree.pos)
            }
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
