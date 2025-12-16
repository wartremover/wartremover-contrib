package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.Apply
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class ApplyTest2 extends AnyFunSuite with ResultAssertions {
  test("TypeTag, reify") {
    val result = WartTestTraverser(Apply) {
      implicitly[scala.reflect.runtime.universe.TypeTag[List[Int]]]
      scala.reflect.runtime.universe.reify(Option(2))
    }
    assertEmpty(result)
  }
}
