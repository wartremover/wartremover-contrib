package org.wartremover
package contrib.test

import munit.FunSuite
import org.wartremover.contrib.warts.MissingOverride
import org.wartremover.test.WartTestTraverser
import scala.annotation.unchecked.uncheckedOverride

class UncheckedOverrideTest extends FunSuite with ResultAssertions {
  test("@uncheckedOverride") {
    val result = WartTestTraverser(MissingOverride) {
      trait T {
        def f(): Unit
      }
      class C extends T {
        @uncheckedOverride def f() = {}
      }
    }
    assertEmpty(result)
  }
}
