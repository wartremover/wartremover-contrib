package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.MissingOverride
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class MissingOverrideTest extends AnyFunSuite with ResultAssertions {
  test("Method must have override modifier") {
    val result = WartTestTraverser(MissingOverride) {
      trait T {
        def f(): Unit
      }
      class C extends T {
        def f() = {}
      }
    }
    assertError(result)("Method must have override modifier")
  }

  test("Explicit override is allowed") {
    val result = WartTestTraverser(MissingOverride) {
      trait T {
        def f(): Unit
      }
      class C extends T {
        override def f() = {}
      }
    }
    assertEmpty(result)
  }

  test("MissingOverride wart obeys SuppressWarnings") {
    val result = WartTestTraverser(MissingOverride) {
      trait T {
        def f(): Unit
      }
      class C extends T {
        @SuppressWarnings(Array("org.wartremover.contrib.warts.MissingOverride"))
        def f() = {}
      }
    }
    assertEmpty(result)
  }

  test("issue #28. PartialFunction") {
    val result = WartTestTraverser(MissingOverride) {
      val list = List(1, 2, 3)
      list.collect { case 3 => "test" }
    }
    assertEmpty(result)
  }

}
