package org.wartremover
package contrib.warts

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

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._
    import u.universe.Flag._

    implicit class TypeOps(self: Type) { // 2.10 compat
      def typeArgs: List[Type] = self match {
        case PolyType(args, _) => args.map(_.typeSignature)
        case TypeRef(_, _, args) => args
        case ExistentialType(_, u) => u.typeArgs
        case _ => List.empty[Type]
      }
    }

    def isJodaTime(s: Symbol): Boolean = s.fullName.startsWith("org.joda.time")

    def isJavaTime(s: Symbol): Boolean = javaTime.contains(s.fullName)

    def isJavaTimeImport(tree: Tree, selectors: List[ImportSelector]): Boolean =
      selectors.map(tree.symbol.fullName + "." + _.name).exists(javaTime.contains)

    new u.Traverser {
      override def traverse(tree: Tree): Unit = tree match {
        // Ignore trees marked by SuppressWarnings
        case t if hasWartAnnotation(u)(t) =>

        // forbid use of any type from org.joda
        case TypeTree() if isJodaTime(tree.symbol) =>
          error(u)(tree.pos, jodaError)

        // forbid use of any type that's part of the old java time API
        case TypeTree() if isJavaTime(tree.symbol) =>
          error(u)(tree.pos, javaError)

        case tt @ TypeTree() =>
          tt.tpe match {
            // forbid org.joda.time types in type bounds
            case TypeBounds(a, b) if isJodaTime(a.typeSymbol) || isJodaTime(b.typeSymbol) =>
              error(u)(tree.pos, jodaError)
            // forbid old java time API types in type bounds
            case TypeBounds(a, b) if isJavaTime(a.typeSymbol) || isJavaTime(b.typeSymbol) =>
              error(u)(tree.pos, javaError)
            // forbid org.joda.time types as type arguments
            case _ if tt.tpe.typeArgs.exists(t => isJodaTime(t.typeSymbol)) =>
              error(u)(tree.pos, jodaError)
            // forbid old java time API types as type arguments
            case _ if tt.tpe.typeArgs.exists(t => isJavaTime(t.typeSymbol)) =>
              error(u)(tree.pos, javaError)
            case _ =>
              super.traverse(tree)
          }

        // forbid use of org.joda.time members
        case Select(qual, name) if qual.toString.startsWith("org.joda.time") =>
          error(u)(tree.pos, jodaError)

        // forbid use of old java time API members
        case Select(qual, name) if javaTime.contains(qual.toString + "." + name) =>
          error(u)(tree.pos, javaError)

        // forbid org.joda.time types in type applications
        case TypeApply(fun, args) if args.exists(t => isJodaTime(t.symbol)) =>
          error(u)(tree.pos, jodaError)

        // forbid old java time API types in type applications
        case TypeApply(fun, args) if args.exists(t => isJavaTime(t.symbol)) =>
          error(u)(tree.pos, javaError)

        case _ =>
          super.traverse(tree)
      }
    }
  }
}
