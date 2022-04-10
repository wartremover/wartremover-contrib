package org.wartremover.contrib.warts

import org.wartremover.WartTraverser
import org.wartremover.WartUniverse
import scala.reflect.ClassTag
import scala.reflect.ManifestFactory

object RefinedClasstag extends WartTraverser {

  private[contrib] def ctMessage(typeName: String): String =
    s"Refined types should not be used in Classtags since only the first type will be checked at runtime. Type found: $typeName"
  private[contrib] def mfMessage(typeName: String): String =
    s"Refined types should not be used in Manifests since only the first type will be checked at runtime. Type found: $typeName"

  def apply(u: WartUniverse): u.Traverser = {
    new u.Traverser(this) {
      import q.reflect.*
      override def traverseTree(tree: Tree)(owner: Symbol): Unit = {
        tree match {
          case _ if hasWartAnnotation(tree) =>
          case _ if tree.isExpr =>
            tree.asExpr match {
              case '{ ClassTag[t1]($clazz) } =>
                TypeRepr.of[t1] match {
                  case t2: AndType =>
                    error(tree.pos, ctMessage(t2.left.show + " with " + t2.right.show))
                  case t2: OrType =>
                    error(tree.pos, ctMessage(t2.show))
                  case _ =>
                    super.traverseTree(tree)(owner)
                }
              case '{ ManifestFactory.classType[t1]($clazz) } =>
                TypeRepr.of[t1] match {
                  case t2: AndType =>
                    error(tree.pos, mfMessage(t2.left.show + " with " + t2.right.show))
                  case t2: OrType =>
                    error(tree.pos, mfMessage(t2.show))
                  case _ =>
                    super.traverseTree(tree)(owner)
                }
              case _ =>
                super.traverseTree(tree)(owner)
            }
          case _ =>
            super.traverseTree(tree)(owner)
        }
      }
    }
  }
}
