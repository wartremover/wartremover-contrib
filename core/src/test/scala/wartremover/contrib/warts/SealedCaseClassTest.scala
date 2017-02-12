package org.wartremover
package contrib.test

import org.scalatest.FunSuite

import org.wartremover.contrib.warts.SealedCaseClass
import org.wartremover.test.WartTestTraverser

class SealedCaseClassTest extends FunSuite {
  test("can't declare sealed case classes") {
    val result = WartTestTraverser(SealedCaseClass) {
      sealed case class Foo(i: Int)
    }
    assertResult(List("[wartremover:SealedCaseClass] case classes must not be sealed"), "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("can declare non-sealed case classes") {
    val result = WartTestTraverser(SealedCaseClass) {
      case class Foo(i: Int)
    }
    assertResult(List.empty, "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("can declare sealed regular classes") {
    val result = WartTestTraverser(SealedCaseClass) {
      sealed class Foo(i: Int)
      sealed trait Bar
      sealed abstract class Baz
    }
    assertResult(List.empty, "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
  test("SealedCaseClass wart obeys SuppressWarnings") {
    val result = WartTestTraverser(SealedCaseClass) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.SealedCaseClass"))
      sealed case class Foo(i: Int)
    }
    assertResult(List.empty, "result.errors")(result.errors)
    assertResult(List.empty, "result.warnings")(result.warnings)
  }
}
