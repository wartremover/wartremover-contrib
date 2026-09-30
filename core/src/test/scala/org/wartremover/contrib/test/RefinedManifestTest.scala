package org.wartremover.contrib.test

import org.wartremover.contrib.warts.RefinedManifest
import org.wartremover.test.WartTestTraverser
import scala.annotation.nowarn
import munit.FunSuite

@nowarn("msg=Manifest")
class RefinedManifestTest extends FunSuite with ResultAssertions {

  import RefinedManifestTest._

  def methodWithManifest[T]()(implicit mf: Manifest[T]): Unit = {}

  private[this] val AwithB = {
    val clazz = this.getClass.getName
    s"${clazz}.A with ${clazz}.B"
  }

  test("can't use refined types with manifests") {
    val result: WartTestTraverser.Result = WartTestTraverser(RefinedManifest) {
      methodWithManifest[A with B]()
    }
    assertError(result)(RefinedManifest.mfMessage(AwithB))
  }

  test("can use single trait or an object in manifests") {
    val result: WartTestTraverser.Result = WartTestTraverser(RefinedManifest) {
      methodWithManifest[A]()
      methodWithManifest[B]()
      methodWithManifest[C]()
      methodWithManifest[Obj.type]()
    }
    assertEmpty(result)
  }

  test("obeys SuppressWarnings") {
    val result: WartTestTraverser.Result = WartTestTraverser(RefinedManifest) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.RefinedManifest"))
      def fun = {
        methodWithManifest[A with B]()
      }
    }
    assertEmpty(result)
  }
}

object RefinedManifestTest {
  trait A

  trait B

  trait C extends A with B

  case object Obj extends A with C
}
