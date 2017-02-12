package org.wartremover.contrib.warts

import org.scalatest.FunSuite
import org.wartremover.contrib.test.ResultAssertions
import org.wartremover.test.WartTestTraverser

class OldTimeTest extends FunSuite with ResultAssertions {

  val javaError = "[wartremover:OldTime] The old Java time API is disabled. Use Java 8 java.time._ API instead."

  val jodaError = "[wartremover:OldTime] JodaTime is disabled. Use Java 8 java.time._ API instead."

  test("disable Joda time wildcard imports") {
    val result = WartTestTraverser(OldTime) {
      import org.joda.time._
    }
    assertError(result)(jodaError)
  }
  test("disable Joda time explicit imports") {
    val result = WartTestTraverser(OldTime) {
      import org.joda.time.LocalDate
    }
    assertError(result)(jodaError)
  }
  test("disable Joda time renamed imports") {
    val result = WartTestTraverser(OldTime) {
      import org.joda.time.{ Instant => Something }
    }
    assertError(result)(jodaError)
  }
  test("disable Joda time erased imports") {
    val result = WartTestTraverser(OldTime) {
      import org.joda.time.{ Instant => _ }
    }
    assertError(result)(jodaError)
  }

  test("disable java.util.Date explicit imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.Date
    }
    assertError(result)(javaError)
  }
  test("disable java.util.Date renamed imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ Date => Something }
    }
    assertError(result)(javaError)
  }
  test("disable java.util.Date erased imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ Date => _ }
    }
    assertError(result)(javaError)
  }

  test("disable java.util.Calendar explicit imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.Calendar
    }
    assertError(result)(javaError)
  }
  test("disable java.util.Calendar renamed imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ Calendar => Something }
    }
    assertError(result)(javaError)
  }
  test("disable java.util.Calendar erased imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ Calendar => _ }
    }
    assertError(result)(javaError)
  }

  test("disable java.util.GregorianCalendar explicit imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.GregorianCalendar
    }
    assertError(result)(javaError)
  }
  test("disable java.util.GregorianCalendar renamed imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ GregorianCalendar => Something }
    }
    assertError(result)(javaError)
  }
  test("disable java.util.GregorianCalendar erased imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ GregorianCalendar => _ }
    }
    assertError(result)(javaError)
  }

  test("disable java.util.TimeZone explicit imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.TimeZone
    }
    assertError(result)(javaError)
  }
  test("disable java.util.TimeZone renamed imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ TimeZone => Something }
    }
    assertError(result)(javaError)
  }
  test("disable java.util.TimeZone erased imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ TimeZone => _ }
    }
    assertError(result)(javaError)
  }

  test("disable java.text.DateFormat explicit imports") {
    val result = WartTestTraverser(OldTime) {
      import java.text.DateFormat
    }
    assertError(result)(javaError)
  }
  test("disable java.text.DateFormat renamed imports") {
    val result = WartTestTraverser(OldTime) {
      import java.text.{ DateFormat => Something }
    }
    assertError(result)(javaError)
  }
  test("disable java.text.DateFormat erased imports") {
    val result = WartTestTraverser(OldTime) {
      import java.text.{ DateFormat => _ }
    }
    assertError(result)(javaError)
  }

  test("disable java.text.SimpleDateFormat explicit imports") {
    val result = WartTestTraverser(OldTime) {
      import java.text.SimpleDateFormat
    }
    assertError(result)(javaError)
  }
  test("disable java.text.SimpleDateFormat renamed imports") {
    val result = WartTestTraverser(OldTime) {
      import java.text.{ SimpleDateFormat => Something }
    }
    assertError(result)(javaError)
  }
  test("disable java.text.SimpleDateFormat erased imports") {
    val result = WartTestTraverser(OldTime) {
      import java.text.{ SimpleDateFormat => _ }
    }
    assertError(result)(javaError)
  }

  test("disable java.util._ combined imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ Date, Calendar, GregorianCalendar, TimeZone }
    }
    assertError(result)(javaError)
  }
  test("disable java.util._ combined multiline imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ Date, Calendar }
      import java.util.{ GregorianCalendar, TimeZone }
    }
    assert(result.errors == List(javaError, javaError))
  }

  test("disable combined java and joda time imports") {
    val result = WartTestTraverser(OldTime) {
      import java.util.{ Date, Calendar }
      import org.joda.time.Interval
    }
    assert(result.errors == List(javaError, jodaError))
  }

  test("still allow importing java.util._") {
    val result = WartTestTraverser(OldTime) {
      import java.util._
    }
    assertEmpty(result)
  }
  test("still allow importing org.joda._") {
    val result = WartTestTraverser(OldTime) {
      import org.joda._
    }
    assertEmpty(result)
  }

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
    assert(result.errors == List(javaError, javaError))
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
    assert(result.errors == List(javaError, javaError))
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
    assert(result.errors == List(jodaError, jodaError))
  }
}
