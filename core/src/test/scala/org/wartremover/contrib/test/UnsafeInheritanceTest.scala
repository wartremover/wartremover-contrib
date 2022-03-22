package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.UnsafeInheritance
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class UnsafeInheritanceTest extends AnyFunSuite with ResultAssertions {
  test("Method must be final or abstract") {
    val result = WartTestTraverser(UnsafeInheritance) {
      trait T {
        def m() = {}
      }
    }
    assertError(result)("Method must be final or abstract")
  }

  test("Final and abstract, private, object methods are allowed") {
    val result = WartTestTraverser(UnsafeInheritance) {
      trait T {
        final def m2() = {}
        def m1(): Unit
        private def m3() = {}
      }
      final class C1 {
        def m() = {}
      }
      sealed class C2 {
        def m() = {}
      }
      object O {
        def m() = {}
      }
      case class CC(i: Int)
    }
    assertEmpty(result)
  }

  test("UnsafeInheritance wart obeys SuppressWarnings") {
    val result = WartTestTraverser(UnsafeInheritance) {
      trait T {
        @SuppressWarnings(Array("org.wartremover.contrib.warts.UnsafeInheritance"))
        def m() = {}
      }
    }
    assertEmpty(result)
  }
}
