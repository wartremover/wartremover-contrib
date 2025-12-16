package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.MissingOverride
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class MissingOverrideTest2 extends AnyFunSuite with ResultAssertions {
  test("TypeTag, reify") {
    val result = WartTestTraverser(MissingOverride) {
      implicitly[scala.reflect.runtime.universe.TypeTag[List[Int]]]
      scala.reflect.runtime.universe.reify(Option(2))
    }
    assertEmpty(result)
  }
}
