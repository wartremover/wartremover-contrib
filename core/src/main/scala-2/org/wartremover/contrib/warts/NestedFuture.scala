package org.wartremover
package contrib.warts

object NestedFuture extends WartTraverser {
  val message: String =
    """`Future[Future[A]]` will not wait for and discard/cancel the inner future.
      |To chain the result of Future to other Future, use flatMap or a for comprehension.
      |""".stripMargin

  private val futureSymbols: Set[String] = Set(
    "scala.concurrent.Future",
    "com.twitter.util.Future"
  )

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    new Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>
          case t: TermTree if futureSymbols.contains(t.tpe.typeSymbol.fullName) =>
            t.tpe.typeArgs match {
              case Seq(singleArg) if singleArg.typeSymbol.fullName == t.tpe.typeSymbol.fullName =>
                warning(u)(tree.pos, message)
                super.traverse(tree)
              case _ => super.traverse(tree)
            }
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
