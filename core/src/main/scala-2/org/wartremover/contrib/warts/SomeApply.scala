package org.wartremover
package contrib.warts

object SomeApply extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val someObject = rootMirror.typeOf[scala.Some.type]
    val someClass = rootMirror.staticClass("scala.Some").toType.typeConstructor

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>

          case Apply(
                TypeApply(Select(obj, TermName("apply")), _),
                _
              ) if obj.tpe.dealias =:= someObject =>
            error(u)(tree.pos, "Some.apply is disabled - use Option.apply instead")

          case New(obj) if obj.tpe.typeConstructor =:= someClass =>
            error(u)(tree.pos, "Some.apply is disabled - use Option.apply instead")

          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
