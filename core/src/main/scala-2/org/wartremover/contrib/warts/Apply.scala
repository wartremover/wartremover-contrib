package org.wartremover
package contrib.warts

object Apply extends WartTraverser {

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val Apply = TermName("apply")

    def isTypeCreatorName(s: TypeName): Boolean = {
      // skip TypeTag
      // https://github.com/scala/scala/blob/41f6cfcd4b05298/src/reflect/scala/reflect/internal/StdNames.scala#L252
      // https://github.com/scala/scala/blob/41f6cfcd4b05298/src/compiler/scala/reflect/reify/utils/Extractors.scala#L23-L99
      val str = s.toString
      val prefix = "$typecreator"
      str.startsWith(prefix) && str.drop(prefix.length).forall(c => '0' <= c && c <= '9')
    }
    def isTreeCreatorName(s: TypeName): Boolean = {
      // https://github.com/scala/scala/blob/41f6cfcd4b05298/src/reflect/scala/reflect/internal/StdNames.scala#L251
      // https://github.com/scala/scala/blob/41f6cfcd4b05298/src/compiler/scala/reflect/reify/utils/Extractors.scala#L23-L99
      val str = s.toString
      val prefix = "$treecreator"
      str.startsWith(prefix) && str.drop(prefix.length).forall(c => '0' <= c && c <= '9')
    }

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>
          case t: ClassDef
              if isTypeCreatorName(t.name) && t.impl.tpe.baseClasses
                .exists(_.fullName == "scala.reflect.api.TypeCreator") =>
          case t: ClassDef
              if isTreeCreatorName(t.name) && t.impl.tpe.baseClasses
                .exists(_.fullName == "scala.reflect.api.TreeCreator") =>
          case t @ DefDef(_, Apply, _, _, _, _) if !isSynthetic(u)(t) && !t.symbol.owner.isModuleClass =>
            error(u)(t.pos, "apply is disabled")
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }

}
