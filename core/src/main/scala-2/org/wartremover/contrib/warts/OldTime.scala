package org.wartremover
package contrib.warts

import scala.collection.mutable

/**
 * Forbids use of
 *  - java.util.{ Date, Calendar, GregorianCalendar, TimeZone }
 *  - java.text.{ DateFormat, SimpleDateFormat }
 *  - org.joda.time._
 */
object OldTime extends WartTraverser {

  val javaError = "The old Java time API is disabled. Use Java 8 java.time._ API instead."

  val jodaError = "JodaTime is disabled. Use Java 8 java.time._ API instead."

  val javaTime: Set[String] = Set(
    "java.util.Date",
    "java.util.Calendar",
    "java.util.GregorianCalendar",
    "java.util.TimeZone",
    "java.text.DateFormat",
    "java.text.SimpleDateFormat"
  )

  private[this] final case class LineInFile(path: String, line: Int)

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._
    import u.universe.Flag._

    def isJodaTime(s: Symbol): Boolean = s.fullName.startsWith("org.joda.time")

    def isJavaTime(s: Symbol): Boolean = javaTime.contains(s.fullName)

    def isJavaTimeImport(tree: Tree, selectors: List[ImportSelector]): Boolean =
      selectors.map(tree.symbol.fullName + "." + _.name).exists(javaTime.contains)

    val linesWithError = mutable.Set.empty[LineInFile]

    def addError(pos: Position, message: String): Unit = {
      error(u)(pos, message)
      linesWithError.add(LineInFile(pos.source.path, pos.line))
    }

    def errorAlreadyExists(pos: Position): Boolean = {
      linesWithError.contains(LineInFile(pos.source.path, pos.line))
    }

    new u.Traverser {
      override def traverse(tree: Tree): Unit = tree match {
        // Ignore trees marked by SuppressWarnings
        case t if hasWartAnnotation(u)(t) =>
        case _ if errorAlreadyExists(tree.pos) =>

        // forbid use of any type from org.joda
        case TypeTree() if isJodaTime(tree.symbol) =>
          addError(tree.pos, jodaError)

        // forbid use of any type that's part of the old java time API
        case TypeTree() if isJavaTime(tree.symbol) =>
          addError(tree.pos, javaError)

        case tt @ TypeTree() =>
          tt.tpe match {
            // forbid org.joda.time types in type bounds
            case TypeBounds(a, b) if isJodaTime(a.typeSymbol) || isJodaTime(b.typeSymbol) =>
              addError(tree.pos, jodaError)
            // forbid old java time API types in type bounds
            case TypeBounds(a, b) if isJavaTime(a.typeSymbol) || isJavaTime(b.typeSymbol) =>
              addError(tree.pos, javaError)
            // forbid org.joda.time types as type arguments
            case _ if tt.tpe.typeArgs.exists(t => isJodaTime(t.typeSymbol)) =>
              addError(tree.pos, jodaError)
            // forbid old java time API types as type arguments
            case _ if tt.tpe.typeArgs.exists(t => isJavaTime(t.typeSymbol)) =>
              addError(tree.pos, javaError)
            case _ =>
              super.traverse(tree)
          }

        // forbid use of org.joda.time members
        case Select(qual, name) if qual.toString.startsWith("org.joda.time") =>
          addError(tree.pos, jodaError)

        // forbid use of old java time API members
        case Select(qual, name) if javaTime.contains(qual.toString + "." + name) =>
          addError(tree.pos, javaError)

        // forbid org.joda.time types in type applications
        case TypeApply(fun, args) if args.exists(t => isJodaTime(t.symbol)) =>
          addError(tree.pos, jodaError)

        // forbid old java time API types in type applications
        case TypeApply(fun, args) if args.exists(t => isJavaTime(t.symbol)) =>
          addError(tree.pos, javaError)

        case _ =>
          super.traverse(tree)
      }
    }
  }
}
