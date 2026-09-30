package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.Apply
import org.wartremover.test.WartTestTraverser
import munit.FunSuite

class ApplyTest2 extends FunSuite with ResultAssertions {
  test("TypeTag, reify") {
    val result = WartTestTraverser(Apply) {
      implicitly[scala.reflect.runtime.universe.TypeTag[List[Int]]]
      scala.reflect.runtime.universe.reify(Option(2))
    }
    assertEmpty(result)
  }
}
