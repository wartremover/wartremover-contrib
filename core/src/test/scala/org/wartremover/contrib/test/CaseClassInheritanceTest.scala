package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.CaseClassInheritance
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class CaseClassInheritanceTest extends AnyFunSuite with ResultAssertions {
  test("case class inheritance disallowed: as class") {
    val result = WartTestTraverser(CaseClassInheritance) {
      case class Car()
      class RedCar() extends Car()
    }
    assertError(result)("Case class should not be inherited: Car")
  }

  test("case class inheritance disallowed: as object") {
    val result = WartTestTraverser(CaseClassInheritance) {
      case class Car()
      object RedCar extends Car()
    }
    assertError(result)("Case class should not be inherited: Car")
  }

  test("regular class inheritance allowed") {
    val result = WartTestTraverser(CaseClassInheritance) {
      class Car()
      class RedCar() extends Car()
      case class BlueCar() extends Car()
      object BlackCar extends Car()
    }
    assertEmpty(result)
  }

  test("obeys SuppressWarnings") {
    val result = WartTestTraverser(CaseClassInheritance) {
      case class Car()
      @SuppressWarnings(Array("org.wartremover.contrib.warts.CaseClassInheritance"))
      class RedCar() extends Car()
      @SuppressWarnings(Array("org.wartremover.contrib.warts.CaseClassInheritance"))
      object BlueCar extends Car()
    }
    assertEmpty(result)
  }
}
