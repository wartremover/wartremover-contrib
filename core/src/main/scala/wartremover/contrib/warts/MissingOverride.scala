package org.wartremover
package contrib.warts

object MissingOverride extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val IsDefinedAt = TermName("isDefinedAt")
    def isPartialFunctionIsDefinedAt(t: DefDef): Boolean = {
      t.name == IsDefinedAt && t.tpt.tpe.typeSymbol.fullName == "scala.Boolean"
    }

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>
          case t: DefDef if t.symbol.overrides.nonEmpty && !t.mods.hasFlag(Flag.OVERRIDE) && !isSynthetic(u)(t) && !isPartialFunctionIsDefinedAt(t) =>
            error(u)(tree.pos, "Method must have override modifier")
            super.traverse(tree)
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
