package org.wartremover
package contrib.warts

import org.scalatest.funsuite.AnyFunSuite
import org.wartremover.contrib.test.ResultAssertions
import org.wartremover.test.WartTestTraverser

class NonPrimitiveInStringInterpolationTest extends AnyFunSuite with ResultAssertions {
  case class Dummy(
    value: String)
  val caseClass = Dummy("world")

  test("""s"hello $str." is OK if the type of `str` is primitive""") {
    val result = WartTestTraverser(NonPrimitiveInStringInterpolation) {
      val str = "world"
      s"hello $str."
    }
    assert(result.errors.isEmpty)
  }

  test("""s"hello $number." is OK if the type of `number` is primitive""") {
    val result = WartTestTraverser(NonPrimitiveInStringInterpolation) {
      val number = 1.0
      s"hello $number."
    }
    assert(result.errors.isEmpty)
  }

  test("""s"hello $boolean." is OK if the type of `boolean` is primitive""") {
    val result = WartTestTraverser(NonPrimitiveInStringInterpolation) {
      val boolean = false
      s"hello $boolean."
    }
    assert(result.errors.isEmpty)
  }

  test("""s"hello $caseClass." is disable if the type of `number` is not primitive""") {
    val result = WartTestTraverser(NonPrimitiveInStringInterpolation) {
      s"hello $caseClass."
    }
    assertError(result)("This string interpolation contains non primitive value. Fix it.")
  }

  test("""s"hello $caseClass.$caseClass." is disabled if `number` is repeated""") {
    val result = WartTestTraverser(NonPrimitiveInStringInterpolation) {
      s"hello $caseClass.$caseClass"
    }
    assertError(result)("This string interpolation contains non primitive value. Fix it.")
  }

  test("`String.+` is OK even if the argument is not primitive") {
    val result = WartTestTraverser(NonPrimitiveInStringInterpolation) {
      "world" + caseClass
    }
    assert(result.errors.isEmpty)
  }

  test("""s"hello ${Dummy("world")}" is disable""") {
    val result = WartTestTraverser(NonPrimitiveInStringInterpolation) {
      s"hello ${Dummy("world")}."
    }
    assertError(result)("This string interpolation contains non primitive value. Fix it.")
  }

  test("""s"hello ${caseClass.value}" is OK if the type of `data.value` is primitive""") {
    val result = WartTestTraverser(NonPrimitiveInStringInterpolation) {
      s"hello ${caseClass.value}."
    }
    assert(result.errors.isEmpty)
  }
}
