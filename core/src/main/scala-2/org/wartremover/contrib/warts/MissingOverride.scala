package org.wartremover
package contrib.warts

object MissingOverride extends WartTraverser {
  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val IsDefinedAt = TermName("isDefinedAt")
    def isPartialFunctionIsDefinedAt(t: DefDef): Boolean = {
      t.name == IsDefinedAt && t.tpt.tpe.typeSymbol.fullName == "scala.Boolean"
    }

    def isTypeCreatorName(s: TypeName): Boolean = {
      // skip TypeTag
      // https://github.com/scala/scala/blob/41f6cfcd4b05298/src/reflect/scala/reflect/internal/StdNames.scala#L252
      // https://github.com/scala/scala/blob/41f6cfcd4b05298/src/compiler/scala/reflect/reify/utils/Extractors.scala#L23-L99
      val str = s.toString
      val prefix = "$typecreator"
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
          case t: DefDef
              if t.symbol.overrides.nonEmpty && !(t.mods.hasFlag(Flag.OVERRIDE) || t.mods.hasFlag(Flag.ABSOVERRIDE)) &&
                !isSynthetic(u)(t) && !isPartialFunctionIsDefinedAt(t) =>
            error(u)(tree.pos, "Method must have override modifier")
            super.traverse(tree)
          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
