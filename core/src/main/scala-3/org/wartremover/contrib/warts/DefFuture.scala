package org.wartremover
package contrib.warts

import scala.concurrent.Future

object DefFuture extends WartTraverser {

  def apply(u: WartUniverse): u.Traverser =
    new u.Traverser(this) {
      import q.reflect.*

      val futureSymbol    = TypeRepr.of[Future[Any]].typeSymbol
      val typeReprNothing = TypeRepr.of[Nothing]

      def isFuture(tpe: TypeRepr): Boolean =
        tpe.baseType(futureSymbol) != typeReprNothing

      override def traverseTree(tree: Tree)(owner: Symbol): Unit =
        tree match {
          // Ignore trees marked by SuppressWarnings
          case _ if hasWartAnnotation(tree) =>

          case valDef @ ValDef(name, tpt, _) if isFuture(tpt.tpe) =>
            val isLazy = valDef.symbol.flags.is(Flags.Lazy)
            error(tree.pos, valErrorMsg(name, if (isLazy) "lazy val" else "val"))

          case _ => super.traverseTree(tree)(owner)
        }
    }

  def valErrorMsg(name: String, valType: String) =
    s"`$name` is a Future `$valType`. Consider using a `def` instead."
}
