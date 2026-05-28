package org.wartremover
package contrib.warts

object ScalaCheckSuchThat extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._
    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          case _ if hasWartAnnotation(u)(tree) =>
          case Select(gen, TermName(method @ ("filter" | "filterNot" | "withFilter" | "suchThat")))
              if gen.tpe.dealias.typeSymbol.fullName == "org.scalacheck.Gen" =>
            error(u)(tree.pos, s"org.scalacheck.Gen.${method} is disabled")
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
