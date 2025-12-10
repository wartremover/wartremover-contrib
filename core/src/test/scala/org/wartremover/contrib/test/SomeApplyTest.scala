package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.SomeApply
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class SomeApplyTest extends AnyFunSuite with ResultAssertions {

  test("can't use Some.apply with null") {
    val result = WartTestTraverser(SomeApply) {
      Some(null)
    }
    assertError(result)("Some.apply is disabled - use Option.apply instead")
  }

  test("can't use Some.apply with a literal") {
    val result = WartTestTraverser(SomeApply) {
      Some(1)
    }
    assertError(result)("Some.apply is disabled - use Option.apply instead")
  }

  test("can't use Some.apply with an identifier") {
    val result = WartTestTraverser(SomeApply) {
      val x = 1
      Some(x)
    }
    assertError(result)("Some.apply is disabled - use Option.apply instead")
  }

  test("new") {
    val result = WartTestTraverser(SomeApply) {
      new Some("b")
    }
    assertError(result)("Some.apply is disabled - use Option.apply instead")
  }

  test("alias") {
    val SomeAlias1 = Some
    import scala.{Some => SomeAlias2}
    Seq(
      WartTestTraverser(SomeApply) {
        SomeAlias1("a")
      },
      WartTestTraverser(SomeApply) {
        SomeAlias2("a")
      },
    ).foreach(result => assertError(result)("Some.apply is disabled - use Option.apply instead"))
  }

  test("can use Some.unapply in pattern matching") {
    val result = WartTestTraverser(SomeApply) {
      Option("test") match {
        case Some(test) => println(test)
        case None => println("not gonna happen")
      }
    }
    assertEmpty(result)
  }

  test("obeys SuppressWarnings") {
    val result = WartTestTraverser(SomeApply) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.SomeApply"))
      val x = Some(null)
    }
    assertEmpty(result)
  }
}
