package org.wartremover
package contrib.warts

object UseFlatMap extends WartTraverser {
  val message =
    """Use flatMap/for-expression instead.
      |""".stripMargin

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val eitherTypeSymbol = typeOf[Either[Any, Any]].typeSymbol
    require(eitherTypeSymbol != NoSymbol)

    val eitherLeftSymbol = typeOf[Left[Any, Any]].typeSymbol
    require(eitherLeftSymbol != NoSymbol)

    def leftShouldUseFlatMap(match_ : Match): Boolean = {
      match_.selector.tpe.typeSymbol == eitherTypeSymbol &&
        (match_.cases.filter(_.pat.tpe.typeSymbol == eitherLeftSymbol) match {
          case Seq(one) =>
            one match {
              case CaseDef(Bind(bindName, Apply(_, _)), EmptyTree, Ident(returnName)) =>
                bindName == returnName
              case CaseDef(Apply(_, List(Bind(bindName, _))), EmptyTree, Apply(ident, List(Ident(appliedName)))) =>
                bindName == appliedName && ident.tpe.finalResultType.typeSymbol == eitherLeftSymbol
              case _ => false
            }
          case _ => false
        })
    }

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          case t if hasWartAnnotation(u)(t) => // Ignore trees marked by SuppressWarnings
          case tr @ Match(_, _) if leftShouldUseFlatMap(tr) =>
            error(u)(tree.pos, message)
            super.traverse(tree)
          case _ => super.traverse(tree)
        }
      }
    }
  }
}
