package org.wartremover
package contrib.warts

import scala.concurrent.Future

object DefFuture extends WartTraverser {

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val futureSymbol = typeOf[Future[Any]]
    val futureTypeSymbol = futureSymbol.typeSymbol
    require(futureTypeSymbol != NoSymbol)

    new Traverser {

      def rshIsFuture(rhs: Tree): Boolean =
        rhs.tpe.typeSymbol == futureTypeSymbol

      override def traverse(tree: Tree): Unit =
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>

          case q"val $name: $tpe = $rhs" if rshIsFuture(rhs) =>
            error(u)(tree.pos, valErrorMsg(name.toString, "val"))

          case q"val $name = $rhs" if rshIsFuture(rhs) =>
            error(u)(tree.pos, valErrorMsg(name.toString, "val"))

          case q"lazy val $name: $tpe = $rhs" if rshIsFuture(rhs) =>
            error(u)(tree.pos, valErrorMsg(name.toString, "lazy val"))

          case q"lazy val $name = $rhs" if rshIsFuture(rhs) =>
            error(u)(tree.pos, valErrorMsg(name.toString, "lazy val"))

          case _ => super.traverse(tree)
        }
    }
  }

  def valErrorMsg(name: String, valType: String) =
    s"`$name` is a Future `$valType`. Consider using a `def` instead."
}
