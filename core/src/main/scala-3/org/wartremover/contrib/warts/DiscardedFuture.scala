package org.wartremover
package contrib.warts

import scala.concurrent.Future

object DiscardedFuture extends WartTraverser {

  val message: String =
    """andThen discards the return value of callback.
      |To chain the result of Future to other Future, use flatMap.
      |""".stripMargin

  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if sourceCodeNotContains(tree, "andThen") =>
          case _ if hasWartAnnotation(tree) =>
          case _ if tree.isExpr =>
            tree.asExpr match {
              case '{
                    type t1
                    ($x1: Future[`t1`]).andThen { $x2: PartialFunction[scala.util.Try[`t1`], Future[t2]] }($ec)
                  } =>
                error(tree.pos, message)
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
