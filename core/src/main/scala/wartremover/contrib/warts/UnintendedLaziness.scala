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

    val genMapLikeSymbol = rootMirror.staticClass("scala.collection.GenMapLike")
    val filterKeys: TermName = "filterKeys"
    val mapValues: TermName = "mapValues"

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

}
