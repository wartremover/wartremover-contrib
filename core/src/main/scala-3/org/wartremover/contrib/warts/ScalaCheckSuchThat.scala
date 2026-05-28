package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse

object ScalaCheckSuchThat extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case Select(gen, method @ ("filter" | "filterNot" | "withFilter" | "suchThat"))
              if gen.tpe.dealias.classSymbol.exists(_.fullName == "org.scalacheck.Gen") =>
            error(tree.pos, s"org.scalacheck.Gen.${method} is disabled")
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
