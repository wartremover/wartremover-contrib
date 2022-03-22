package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.Apply
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class ApplyTest extends AnyFunSuite with ResultAssertions {
  test("apply is disabled") {
    val result = WartTestTraverser(Apply) {
      class c {
        def apply() = 1
      }
    }
    assertError(result)("apply is disabled")
  }

  test("object's apply is enabled") {
    val result = WartTestTraverser(Apply) {
      object c {
        def apply() = 1
      }
    }
    assertEmpty(result)
  }

  test("Apply wart obeys SuppressWarnings") {
    val result = WartTestTraverser(Apply) {
      class c {
        @SuppressWarnings(Array("org.wartremover.contrib.warts.Apply"))
        def apply() = 1
      }
    }
    assertEmpty(result)
  }
}
