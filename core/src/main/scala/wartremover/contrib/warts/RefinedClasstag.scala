package org.wartremover.contrib.warts

import org.wartremover.{ WartTraverser, WartUniverse }

object RefinedClasstag extends WartTraverser {

  def ctMessage(typeName: String): String = s"Refined types should not be used in Classtags since only the first type will be checked at runtime. Type found: $typeName"
  def mfMessage(typeName: String): String = s"Refined types should not be used in Manifests since only the first type will be checked at runtime. Type found: $typeName"

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    object RefinedTypeTree {
      def unapply(typ: Tree): Option[Type] = {
        typ.tpe match {
          case RefinedType(_) => Some(typ.tpe)
          case _ => None
        }
      }
    }

    val classTag: TermName = "ClassTag"
    val applyMethod: TermName = "apply"
    val scala: TermName = "scala"
    val reflect: TermName = "reflect"
    val manifestFactory: TermName = "ManifestFactory"
    val intersectionType: TermName = "intersectionType"

    new u.Traverser {

      override def traverse(tree: Tree): Unit = {

        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>

          case TypeApply(Select(Ident(`classTag`), `applyMethod`), args) =>
            args.foreach {
              case arg @ RefinedTypeTree(tpe) =>
                error(u)(arg.pos, ctMessage(tpe.toString))
              case _ =>
            }
            super.traverse(tree)

          case TypeApply(Select(Select(Select(Ident(`scala`), `reflect`), `manifestFactory`), `intersectionType`), args) =>
            args.foreach {
              case arg @ RefinedTypeTree(tpe) =>
                error(u)(arg.pos, mfMessage(tpe.toString))
              case _ =>
            }
            super.traverse(tree)

          case _ =>
            super.traverse(tree)
        }
      }
    }
  }

}
