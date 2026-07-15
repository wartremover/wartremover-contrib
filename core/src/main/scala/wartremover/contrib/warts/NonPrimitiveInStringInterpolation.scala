package org.wartremover
package contrib.warts

import scala.annotation.tailrec

object NonPrimitiveInStringInterpolation extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    @tailrec
    def checkArgumentsTypeIsPrimitive(args: List[Tree]): Boolean = {
      args match {
        case Nil => true

        case h :: t =>
          if (h.tpe <:< typeOf[String] || h.tpe <:< typeOf[Int] || h.tpe <:< typeOf[Double] ||
            h.tpe <:< typeOf[Long] || h.tpe <:< typeOf[Boolean] || h.tpe <:< typeOf[Float])
            checkArgumentsTypeIsPrimitive(t)
          else
            false
      }
    }

    /**
     * Collect arguments from nested `Apply(Select(_, TermName("$plus")), args)`
     *
     * @param tree AST
     * @param acc Arguments accumulator
     * @return None       The input `tree` contains something
     *                    that is NOT `Apply(Select(_, TermName("$plus")), args)`
     *         Some(args) The `tree` consists of only `Apply(Select(_, TermName("$plus")), args)` form
     *                    and `args` is all arguments of the `tree`
     */
    @tailrec
    def collectArgsFormNestedApplySelectPlus(tree: Tree, acc: List[Tree]): Option[List[Tree]] =
      tree match {
        case Apply(Select(t @ Literal(_), TermName("$plus")), args) if t.tpe <:< typeOf[String] =>
          Some(args ++ acc)

        case Apply(Select(t, TermName("$plus")), args) =>
          collectArgsFormNestedApplySelectPlus(t, args ++ acc)

        case _ =>
          None
      }

    val message = "This string interpolation contains non primitive value. Fix it."
    new Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          case t if hasWartAnnotation(u)(t) =>
          case Apply(Select(Apply(Select(t, TermName("apply")), _), TermName("s")), args) if t.symbol.fullName == "scala.StringContext" && !checkArgumentsTypeIsPrimitive(args) =>
            error(u)(tree.pos, message)

          case Typed(t, TypeTree()) =>
            /**
             * From Scala 2.13, [[scala.tools.reflect.FastStringInterpolator]] is rewrite simple
             * `StringContext.apply` tree into the strings appending form.
             */
            collectArgsFormNestedApplySelectPlus(t, Nil) match {
              case Some(args) if !checkArgumentsTypeIsPrimitive(args) =>
                error(u)(tree.pos, message)
              case _ =>
            }
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
