package org.wartremover
package contrib.warts

object UnsafeInheritance extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*

      private def overridableImplementation(t: DefDef) = {
        val f = t.symbol.flags

        !f.is(Flags.Synthetic) &&
        !f.is(Flags.Final) &&
        !f.is(Flags.Private) &&
        t.symbol.owner.isClassDef && {
          val c = t.symbol.owner.flags
          !c.is(Flags.Final) && !c.is(Flags.Sealed) && !c.is(Flags.Private) && !c.is(Flags.Protected)
        }
      }

      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case t: DefDef if t.rhs.nonEmpty && !t.symbol.isClassConstructor && overridableImplementation(t) =>
            error(t.pos, "Method must be final or abstract")
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
