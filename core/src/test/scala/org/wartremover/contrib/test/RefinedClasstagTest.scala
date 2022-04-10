package org.wartremover.contrib.test

import org.wartremover.contrib.warts.RefinedClasstag
import org.wartremover.test.WartTestTraverser
import scala.reflect.ClassTag
import org.scalatest.funsuite.AnyFunSuite

class RefinedClasstagTest extends AnyFunSuite with ResultAssertions {

  import RefinedClasstagTest._

  def methodWithClassTag[T]()(implicit ct: ClassTag[T]): Unit = {}

  def methodWithManifest[T]()(implicit mf: Manifest[T]): Unit = {}

  private[this] val AwithB = {
    val clazz = this.getClass.getName
    s"${clazz}.A with ${clazz}.B"
  }
  test("can't use refined types with classTags") {
    val result: WartTestTraverser.Result = WartTestTraverser(RefinedClasstag) {
      methodWithClassTag[A with B]()
    }
    assertError(result)(RefinedClasstag.ctMessage(AwithB))
  }

  test("can't use refined types with manifests") {
    val result: WartTestTraverser.Result = WartTestTraverser(RefinedClasstag) {
      methodWithManifest[A with B]()
    }
    assertError(result)(RefinedClasstag.mfMessage(AwithB))
  }

  test("can use single trait or an object in classtags") {
    val result: WartTestTraverser.Result = WartTestTraverser(RefinedClasstag) {
      methodWithClassTag[A]()
      methodWithClassTag[B]()
      methodWithClassTag[C]()
      methodWithClassTag[Obj.type]()
    }
    assertEmpty(result)
  }

  test("can use single trait or an object in manifests") {
    val result: WartTestTraverser.Result = WartTestTraverser(RefinedClasstag) {
      methodWithManifest[A]()
      methodWithManifest[B]()
      methodWithManifest[C]()
      methodWithManifest[Obj.type]()
    }
    assertEmpty(result)
  }

  test("obeys SuppressWarnings") {
    val result: WartTestTraverser.Result = WartTestTraverser(RefinedClasstag) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.RefinedClasstag"))
      def fun = {
        methodWithClassTag[A with B]()
      }
    }
    assertEmpty(result)
  }
}

object RefinedClasstagTest {
  trait A

  trait B

  trait C extends A with B

  case object Obj extends A with C

  type Ab = A with B

}
