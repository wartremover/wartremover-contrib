package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.SealedCaseClass
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class SealedCaseClassTest extends AnyFunSuite with ResultAssertions {
  test("can't declare sealed case classes") {
    val result = WartTestTraverser(SealedCaseClass) {
      sealed case class Foo(i: Int)
    }
    assertError(result)("case classes must not be sealed")
  }

  test("can declare non-sealed case classes") {
    val result = WartTestTraverser(SealedCaseClass) {
      case class Foo(i: Int)
    }
    assertEmpty(result)
  }

  test("can declare sealed regular classes") {
    val result = WartTestTraverser(SealedCaseClass) {
      sealed class Foo(i: Int)
      sealed trait Bar
      sealed abstract class Baz
    }
    assertEmpty(result)
  }

  test("SealedCaseClass wart obeys SuppressWarnings") {
    val result = WartTestTraverser(SealedCaseClass) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.SealedCaseClass"))
      sealed case class Foo(i: Int)
    }
    assertEmpty(result)
  }
}
