package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse

object Apply extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case t @ DefDef("apply", _, _, _) if !t.symbol.flags.is(Flags.Synthetic) && !owner.flags.is(Flags.Module) =>
            error(tree.pos, "apply is disabled")
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
