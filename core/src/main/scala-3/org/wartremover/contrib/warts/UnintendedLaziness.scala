package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse

object UnintendedLaziness extends WartTraverser {
  val (errorForFilterKeys, errorForMapValues) = {
    def err(name: String) =
      s"""Map#$name is disabled because it implicitly creates lazily evaluated collections.
         |To create lazy collections, use the explicit view method""".stripMargin

    (err("filterKeys"), err("mapValues"))
  }

  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case _ if tree.isExpr =>
            tree.asExpr match {
              case '{ ($x: collection.Map[t1, t2]).filterKeys($f1) } =>
                error(tree.pos, errorForFilterKeys)
              case '{ ($x: collection.Map[t1, t2]).mapValues($f1) } =>
                error(tree.pos, errorForMapValues)
              case _ =>
                super.traverseTree(tree)(owner)
            }
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
