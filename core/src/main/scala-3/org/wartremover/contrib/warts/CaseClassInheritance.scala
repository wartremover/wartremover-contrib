package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse

object CaseClassInheritance extends WartTraverser {
  override def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        def checkClass(c: Symbol): Unit = {
          val caseBases =
            c.typeRef.baseClasses.filter(base => base != c && base.isClassDef && base.flags.is(Flags.Case))
          if (caseBases.nonEmpty) {
            error(tree.pos, s"Case class should not be inherited: ${caseBases.map(_.name).mkString(",")}")
          }
        }

        tree match {
          case t if hasWartAnnotation(t) =>
          // Ignore trees marked by SuppressWarnings.
          case d: ClassDef =>
            checkClass(d.symbol)
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
