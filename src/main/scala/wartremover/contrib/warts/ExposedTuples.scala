package org.wartremover.contrib.warts

import org.wartremover.{ WartTraverser, WartUniverse }

object ExposedTuples extends WartTraverser {
  val message: String =
    """Avoid using tuples in public interfaces, as they only supply type information.
      |Consider using a custom case class to add semantic meaning.""".stripMargin

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    def typeRefContainsTuple(typeRef: TypeRef): Boolean = {
      val TypeRef(_, sym, args) = typeRef

      if (sym.fullName.matches("scala\\.Tuple[\\d]+")) {
        true
      } else {
        args.exists {
          case nextTypeTree: TypeTree => typeTreeContainsTuple(nextTypeTree)
          case nextTypeRef: TypeRef => typeRefContainsTuple(nextTypeRef)
          case _ => false
        }
      }
    }

    def typeTreeContainsTuple(typeTree: TypeTree): Boolean = {
      typeTree.tpe match {
        case typeRef: TypeRef => typeRefContainsTuple(typeRef)
        case _ => false
      }
    }

    def valDefContainsTuple(valDef: u.universe.ValDef): Boolean = {
      valDef.tpt match {
        case typeTree: TypeTree => typeTreeContainsTuple(typeTree)
        case _ => false
      }
    }

    val publicUnscopedValues = Seq(NoFlags, Flag.MUTABLE, Flag.IMPLICIT, Flag.MUTABLE | Flag.IMPLICIT)

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>

          // Return values
          case DefDef(modifiers, name, _, _, returnType: TypeTree, _) if !modifiers.hasFlag(Flag.PRIVATE) && name.toString != "unapply" && typeTreeContainsTuple(returnType) =>
            u.error(tree.pos, message)

          // Parameters
          case DefDef(modifiers, _, _, parameterLists, _, _) if !modifiers.hasFlag(Flag.PRIVATE) && parameterLists.exists(_.exists(valDefContainsTuple)) =>
            u.error(tree.pos, message)

          // Val/var declarations that are not covered by the above definitions
          case ValDef(modifiers, _, returnType: TypeTree, _) if publicUnscopedValues.contains(modifiers.flags) && typeTreeContainsTuple(returnType) =>
            u.error(tree.pos, message)

          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
