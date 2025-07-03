package org.wartremover
package contrib.warts

object CaseClassInheritance extends WartTraverser {
  override def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser {
      override def traverse(tree: u.universe.Tree): Unit = {
        def checkClass(c: u.universe.ClassSymbol): Unit = {
          val caseBases = c.baseClasses
            .filter(base => base != c && base.isClass && base.asClass.isCaseClass)
          if (caseBases.nonEmpty) {
            error(u)(
              tree.pos,
              s"Case class should not be inherited: ${caseBases.map(_.name).mkString(",")}")
          }
        }

        import u.universe._

        tree match {
          case t if hasWartAnnotation(u)(t) =>
          // Ignore trees marked by SuppressWarnings.
          case d: ModuleDef =>
            val m = d.symbol.asModule.moduleClass.asClass
            checkClass(m)
          case d: ClassDef =>
            val c = d.symbol.asClass
            checkClass(c)
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
