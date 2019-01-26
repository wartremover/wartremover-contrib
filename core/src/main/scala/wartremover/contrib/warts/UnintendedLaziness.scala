package org.wartremover
package contrib.warts

object UnintendedLaziness extends WartTraverser {
  val (errorForFilterKeys: String, errorForMapValues: String) = {
    def error(name: String) =
      s"""GenMapLike#$name is disabled because it implicitly creates lazily evaluated collections.
         |To create lazy collections, use the explicit view or toStream methods""".stripMargin

    (error("filterKeys"), error("mapValues"))
  }

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val maybeGenMapLikeSymbol =
      try {
        Option(rootMirror.staticClass("scala.collection.GenMapLike"))
      } catch {
        case _: ScalaReflectionException =>
          // If this happens, the type does not exist, in which case we're using 2.13+, where this wart is a no-op
          None
      }

    maybeGenMapLikeSymbol
      .map { genMapLikeSymbol =>
        val filterKeys = TermName("filterKeys")
        val mapValues = TermName("mapValues")

        new u.Traverser {
          override def traverse(tree: Tree): Unit = {
            tree match {
              // Ignore trees marked by SuppressWarnings
              case t if hasWartAnnotation(u)(t) =>

              case t @ Apply(Select(map, `filterKeys`), _) if map.tpe.baseType(genMapLikeSymbol) != NoType =>
                error(u)(t.pos, errorForFilterKeys)

              case t @ Apply(TypeApply(Select(map, `mapValues`), _), _) if map.tpe.baseType(genMapLikeSymbol) != NoType =>
                error(u)(t.pos, errorForMapValues)

              case _ =>
                super.traverse(tree)
            }
          }
        }
      }
      .getOrElse(new u.Traverser)
  }
}
