package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse
import scala.collection.mutable

object OldTime extends WartTraverser {
  private val oldJavaTime: Set[String] = Set(
    "java.util.Date",
    "java.util.Calendar",
    "java.util.GregorianCalendar",
    "java.util.TimeZone",
    "java.text.DateFormat",
    "java.text.SimpleDateFormat"
  )

  private final case class LineInFile(content: Option[String], startLine: Int)

  private def oldJavaMessage = "The old Java time API is disabled. Use Java 8 java.time._ API instead."
  private def jodaMessage = "JodaTime is disabled. Use Java 8 java.time._ API instead."

  def apply(u: WartUniverse): u.Traverser = {
    val linesWithError = mutable.Set.empty[LineInFile]
    new u.Traverser(this) {
      import q.reflect.*

      def addError(pos: Position, message: String): Unit = {
        error(pos, message)
        linesWithError.add(LineInFile(pos.sourceFile.content, pos.startLine))
      }

      def errorAlreadyExists(pos: Position): Boolean = {
        linesWithError.contains(LineInFile(pos.sourceFile.content, pos.startLine))
      }

      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case _ if errorAlreadyExists(tree.pos) =>
          case t: TypeTree if t.symbol.fullName.startsWith("org.joda.time") =>
            addError(t.pos, jodaMessage)
          case t: TypeTree if oldJavaTime(t.symbol.fullName) =>
            addError(t.pos, oldJavaMessage)
          case t: New if t.tpt.symbol.fullName.startsWith("org.joda.time") =>
            addError(t.pos, jodaMessage)
          case t: New if oldJavaTime(t.tpt.symbol.fullName) =>
            addError(t.pos, oldJavaMessage)
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
