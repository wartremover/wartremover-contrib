package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse

object SymbolicName extends WartTraverser {
  private[this] val validChars = "[a-zA-Z_]+".r
  private[this] def isSymbolic(name: String): Boolean = validChars.replaceAllIn(name, "").lengthIs > 2

  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case t: DefDef
              if !t.symbol.flags.is(Flags.Synthetic) && isSymbolic(t.name) && sourceCodeContains(t, t.name) =>
            error(tree.pos, "Symbolic name is disabled")
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
