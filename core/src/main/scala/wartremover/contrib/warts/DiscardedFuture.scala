package org.wartremover
package contrib.warts

import scala.concurrent.Future

object DiscardedFuture extends WartTraverser {

  val message: String =
    """andThen discards the return value of callback.
      |To chain the result of Future to other Future, use flatMap.
      |""".stripMargin

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val andThenMethodName: TermName = TermName("andThen")
    val futureSymbol = typeOf[Future[Any]]
    val andThenMethod = futureSymbol.member(andThenMethodName)
    val futureTypeSymbol = futureSymbol.typeSymbol
    require(andThenMethod != NoSymbol)
    require(futureTypeSymbol != NoSymbol)

    new Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>
          case Apply(Apply(method, List(callback)), _) if method.symbol == andThenMethod && callback.tpe
            .typeArgs(1)
            .typeSymbol == futureTypeSymbol =>
            error(u)(tree.pos, message)
            super.traverse(tree)
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
