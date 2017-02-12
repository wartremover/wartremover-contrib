package org.wartremover.contrib.warts

import org.scalatest.FunSuite
import org.wartremover.contrib.test.ResultAssertions
import org.wartremover.test.WartTestTraverser

class SomeApplyTest extends FunSuite with ResultAssertions {

  test("can't use Some.apply with null") {
    val result = WartTestTraverser(SomeApply) {
      Some(null)
    }
    assertError(result)("[wartremover:SomeApply] Some.apply is disabled - use Option.apply instead")
  }
  test("can't use Some.apply with a literal") {
    val result = WartTestTraverser(SomeApply) {
      Some(1)
    }
    assertError(result)("[wartremover:SomeApply] Some.apply is disabled - use Option.apply instead")
  }
  test("can't use Some.apply with an identifier") {
    val result = WartTestTraverser(SomeApply) {
      val x = 1
      Some(x)
    }
    assertError(result)("[wartremover:SomeApply] Some.apply is disabled - use Option.apply instead")
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
