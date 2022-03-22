package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse

object SealedCaseClass extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case t if hasWartAnnotation(t) =>
          case t: ClassDef
              if !t.symbol.flags.is(Flags.Synthetic) && t.symbol.isClassDef && t.symbol.flags.is(
                Flags.Case
              ) && t.symbol.flags.is(Flags.Sealed) =>
            error(tree.pos, "case classes must not be sealed")
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
