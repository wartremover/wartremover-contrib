package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.SymbolicName
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class SymbolicNameTest extends AnyFunSuite with ResultAssertions {
  test("Symbolic name is disabled") {
    val result = WartTestTraverser(SymbolicName) {
      class \&/ {
        def &&&() = {}
      }
    }
    assertError(result)("Symbolic name is disabled")
  }

  test("Short symbolic name is allowed") {
    val result = WartTestTraverser(SymbolicName) {
      def ::() = {}
    }
    assertEmpty(result)
  }

  test("All third-party names are allowed") {
    val result = WartTestTraverser(SymbolicName) {
      List(1) ::: List(2)
    }
    assertEmpty(result)
  }

  test("Normal names are allowed") {
    val result = WartTestTraverser(SymbolicName) {
      val name = 0
      val _aB = 1
      val `typ` = 2
    }
    assertEmpty(result)
  }

  test("SymbolicName wart obeys SuppressWarnings") {
    val result = WartTestTraverser(SymbolicName) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.SymbolicName"))
      def :+:() = {}
    }
    assertEmpty(result)
  }

  test("issue 48") {
    // https://github.com/wartremover/wartremover-contrib/issues/48
    val result = WartTestTraverser(SymbolicName) {
      case class Foo(
        a: Boolean = false,
        b: Boolean = false,
        c: Boolean = false,
        d: Boolean = false)
      val a = Foo(a = true)
      val b = Foo(b = true)
      val c = Foo(c = true)
      val d = Foo(d = true)
    }
    assertEmpty(result)
  }

}
