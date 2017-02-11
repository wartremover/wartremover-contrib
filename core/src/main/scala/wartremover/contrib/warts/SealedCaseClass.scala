package org.wartremover
package contrib.warts

object SealedCaseClass extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._
    import u.universe.Flag._

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>
          case ClassDef(mods, _, _, _) if mods.hasFlag(CASE) && mods.hasFlag(SEALED) =>
            u.error(tree.pos, "case classes must not be sealed")
          case t => super.traverse(tree)
        }
      }
    }
  }
}
