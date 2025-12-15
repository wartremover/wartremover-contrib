package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.MissingOverride
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class MissingOverrideTest2 extends AnyFunSuite with ResultAssertions {
  test("TypeTag") {
    val result = WartTestTraverser(MissingOverride) {
      implicitly[scala.reflect.runtime.universe.TypeTag[List[Int]]]
    }
    assertEmpty(result)
  }
}
