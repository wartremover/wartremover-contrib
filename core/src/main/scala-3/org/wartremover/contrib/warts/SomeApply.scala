package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse

object SomeApply extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case Apply(TypeApply(Select(some, "apply"), _), _ :: Nil) if some.tpe =:= TypeRepr.of[Some.type] =>
            error(tree.pos, "Some.apply is disabled - use Option.apply instead")
          case t: New if t.tpe.typeSymbol.fullName == "scala.Some" =>
            error(tree.pos, "Some.apply is disabled - use Option.apply instead")
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
