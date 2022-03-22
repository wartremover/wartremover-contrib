package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse

object MissingOverride extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      private[this] def isPartialFunctionIsDefinedAt(t: DefDef): Boolean =
        (t.name == "isDefinedAt") && (t.returnTpt.tpe =:= TypeRepr.of[Boolean])

      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case t: DefDef
              if !t.symbol.flags.is(Flags.Override) && !t.symbol.flags.is(
                Flags.Synthetic
              ) && !isPartialFunctionIsDefinedAt(t) && t.symbol.allOverriddenSymbols.nonEmpty =>
            error(tree.pos, "Method must have override modifier")
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
