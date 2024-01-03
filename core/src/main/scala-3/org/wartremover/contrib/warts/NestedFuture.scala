package org.wartremover
package contrib.warts

import scala.concurrent.Future

object NestedFuture extends WartTraverser {
  val message: String =
    """`Future[Future[A]]` will not wait for and discard/cancel the inner future.
      |To chain the result of Future to other Future, use flatMap or a for comprehension.
      |""".stripMargin

  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*

      override def traverseTree(tree: Tree)(owner: Symbol): Unit =
        tree match {
          case _ if tree.isExpr =>
            tree.asExpr match {
              case '{ scala.Predef.??? } => super.traverseTree(tree)(owner)
              case '{
                    type a
                    $f: Future[Future[`a`]]
                  } =>
                warning(tree.pos, message)
                super.traverseTree(tree)(owner)
              case _ => super.traverseTree(tree)(owner)
            }
          case _ => super.traverseTree(tree)(owner)
        }
    }
  }
}
