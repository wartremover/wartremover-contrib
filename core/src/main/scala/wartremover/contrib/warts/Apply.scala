package org.wartremover
package contrib.warts

object Apply extends WartTraverser {

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val Apply: TermName = "apply"

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>
          case t @ DefDef(_, Apply, _, _, _, _) if !isSynthetic(u)(t) && !t.symbol.owner.isModuleClass =>
            error(u)(t.pos, "apply is disabled")
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }

}
