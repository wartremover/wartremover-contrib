package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.OldTime
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class OldTimeTest extends AnyFunSuite with ResultAssertions {

  val javaError = "The old Java time API is disabled. Use Java 8 java.time._ API instead."

  val jodaError = "JodaTime is disabled. Use Java 8 java.time._ API instead."

  test("disable use of java.util.Date as a val (1)") {
    val result = WartTestTraverser(OldTime) {
      import java.util._
      val x: Date = ???
    }
    assertError(result)(javaError)
  }
  test("disable use of java.util.Date as a val (2)") {
    val result = WartTestTraverser(OldTime) {
      import java.util._
      val x = new Date()
    }
    assertErrors(result)(javaError, 2)
  }
  test("disable creating instances of java.util.Date (1)") {
    val result = WartTestTraverser(OldTime) {
      import java.util._
      new Date()
    }
    assertError(result)(javaError)
  }
  test("disable creating instances of java.util.Date (2)") {
    val result = WartTestTraverser(OldTime) {
      import java.util._
      val x: Object = new Date()
    }
    assertError(result)(javaError)
  }

  test("disable aliasing java.util.Date") {
    val result = WartTestTraverser(OldTime) {
      type X = java.util.Date
    }
    assertError(result)(javaError)
  }
  test("disable aliasing org.joda.time.LocalDate") {
    val result = WartTestTraverser(OldTime) {
      type X = org.joda.time.LocalDate
    }
    assertError(result)(jodaError)
  }

  test("disable using java.util.Date as a lower type bound") {
    val result = WartTestTraverser(OldTime) {
      def x[A >: java.util.Date](a: A): Unit = ()
    }
    assertError(result)(javaError)
  }
  test("disable using org.joda.time.LocalDate as a lower type bound") {
    val result = WartTestTraverser(OldTime) {
      def x[A >: org.joda.time.LocalDate](a: A): Unit = ()
    }
    assertError(result)(jodaError)
  }

  test("disable using java.util.Date as an upper type bound") {
    val result = WartTestTraverser(OldTime) {
      def x[A <: java.util.Date](a: A): Unit = ()
    }
    assertError(result)(javaError)
  }
  test("disable using org.joda.time.LocalDate as an upper type bound") {
    val result = WartTestTraverser(OldTime) {
      def x[A <: org.joda.time.LocalDate](a: A): Unit = ()
    }
    assertError(result)(jodaError)
  }

  test("disable using java.util.Date as a function return type") {
    val result = WartTestTraverser(OldTime) {
      def x(): java.util.Date = ???
    }
    assertError(result)(javaError)
  }
  test("disable using org.joda.time.LocalDate as a function return type") {
    val result = WartTestTraverser(OldTime) {
      def x(): org.joda.time.LocalDate = ???
    }
    assertError(result)(jodaError)
  }

  test("disable using java.util.Date as a function argument type") {
    val result = WartTestTraverser(OldTime) {
      def x(a: java.util.Date): Unit = ()
    }
    assertError(result)(javaError)
  }
  test("disable using org.joda.time.LocalDate as a function argument type") {
    val result = WartTestTraverser(OldTime) {
      def x(a: org.joda.time.LocalDate): Unit = ()
    }
    assertError(result)(jodaError)
  }

  test("disable using java.util.Date as a type parameter (1)") {
    val result = WartTestTraverser(OldTime) {
      val x: List[java.util.Date] = List.empty
    }
    assertError(result)(javaError)
  }
  test("disable using java.util.Date as a type parameter (2)") {
    val result = WartTestTraverser(OldTime) {
      val x = List.empty[java.util.Date]
    }
    assertErrors(result)(javaError, 2)
  }
  test("disable using org.joda.time.LocalDate as a type parameter (1)") {
    val result = WartTestTraverser(OldTime) {
      val x: List[org.joda.time.LocalDate] = List.empty
    }
    assertError(result)(jodaError)
  }
  test("disable using org.joda.time.LocalDate as a type parameter (2)") {
    val result = WartTestTraverser(OldTime) {
      val x = List.empty[org.joda.time.LocalDate]
    }
    assertErrors(result)(jodaError, 2)
  }
}
