package org.wartremover
package contrib.warts

import scala.collection.mutable

object ExposedTuples extends WartTraverser {
  val message: String =
    "Avoid using tuples in public interfaces, as they only supply type information. Consider using a custom case class to add semantic meaning."

  private final case class LineInFile(path: String, line: Int)

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val linesWithError = mutable.Set.empty[LineInFile]

    def addError(pos: Position): Unit = {
      try {
        error(u)(pos, message)
        linesWithError.add(LineInFile(pos.source.path, pos.line))
      } catch {
        case _: UnsupportedOperationException =>
        // Not supported in 2.10.x but we also don't need deduplication in that version anyway
      }
    }

    def errorAlreadyExists(pos: Position): Boolean = {
      try {
        linesWithError.contains(LineInFile(pos.source.path, pos.line))
      } catch {
        case _: UnsupportedOperationException =>
          // Not supported in 2.10.x but we also don't need deduplication in that version anyway
          false
      }
    }

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

    // No FlagOps.& :(
    val publicUnscopedValues = Seq(
      NoFlags, Flag.IMPLICIT,
      Flag.MUTABLE, Flag.MUTABLE | Flag.IMPLICIT,
      Flag.LAZY, Flag.LAZY | Flag.IMPLICIT,
      Flag.PROTECTED | Flag.LAZY, Flag.PROTECTED | Flag.LAZY | Flag.IMPLICIT)

    new u.Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          // Ignore trees marked by SuppressWarnings
          case t if hasWartAnnotation(u)(t) =>

          // Do not print out multiple errors for the same line, since internal implementation of vals and vars may
          // cause this. Do not traverse into these places since we wouldn't have done anyway.
          case _ if errorAlreadyExists(tree.pos) =>

          // Return values
          case DefDef(modifiers, name, _, _, returnType: TypeTree, _) if !modifiers.hasFlag(Flag.PRIVATE) && !modifiers.hasFlag(Flag.LOCAL) && name.toString != "unapply" && typeTreeContainsTuple(returnType) =>
            addError(tree.pos)

          // Parameters
          case DefDef(modifiers, _, _, parameterLists, _, _) if !modifiers.hasFlag(Flag.PRIVATE) && !modifiers.hasFlag(Flag.LOCAL) && parameterLists.exists(_.exists(valDefContainsTuple)) =>
            addError(tree.pos)

          // Val/var declarations that are not covered by the above definitions
          case ValDef(modifiers, _, returnType: TypeTree, _) if publicUnscopedValues.contains(modifiers.flags) && typeTreeContainsTuple(returnType) =>
            addError(tree.pos)

          // Do not traverse into value / variable / lazy values and method definitions since nothing inside them is
          // publicly exposed.
          case _: ValOrDefDef =>

          case _ =>
            super.traverse(tree)
        }
      }
    }
  }
}
