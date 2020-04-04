package org.wartremover
package contrib.test

import org.scalatest.funspec.AnyFunSpec
import org.wartremover.contrib.warts.UseFlatMap
import org.wartremover.test.WartTestTraverser

class UseFlatMapTest extends AnyFunSpec with ResultAssertions {
  implicit val ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.global

  def foo(): Either[String, Int] = ???

  it("can suppress warnings") {
    val result = WartTestTraverser(UseFlatMap) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.UseFlatMap"))
      def m() = {
        val x: Either[String, Int] = foo() match {
          case Left(a) => Left(a)
          case Right(b) => Right(b * 2)
        }
      }
    }
    assertEmpty(result)
  }

  describe("Either.match") {
    describe("bound to term") {
      it("should be error when returning bound value: Left") {
        val hasError = WartTestTraverser(UseFlatMap) {
          val x: Either[String, Int] = foo() match {
            case y @ Left(_) => y
            case Right(z) => Right(z * 2)
          }
        }
        assertError(hasError)(UseFlatMap.message)
      }

      it("should be error when returning bound value: scala.util.Left") {
        val hasError = WartTestTraverser(UseFlatMap) {
          val x: Either[String, Int] = foo() match {
            case y @ scala.util.Left(_) => y
            case Right(z) => Right(z * 2)
          }
        }
        assertError(hasError)(UseFlatMap.message)
      }

      it("should not be error when returning bound value with some effect") {
        val noError = WartTestTraverser(UseFlatMap) {
          val x: Either[String, Int] = foo() match {
            case y @ Left(_) => y.map(_ * 2)
            case Right(z) => Right(z * 2)
          }
        }
        assertEmpty(noError)
      }
    }

    describe("destructuring Left") {
      it("should be error if returning as-is") {
        val hasError = WartTestTraverser(UseFlatMap) {
          val x: Either[String, Int] = foo() match {
            case Left(a) => Left(a)
            case Right(b) => Right(b * 2)
          }
        }
        assertError(hasError)(UseFlatMap.message)
      }

      it("should not be error if modifying Left") {
        val noError = WartTestTraverser(UseFlatMap) {
          val x: Either[String, Int] = foo() match {
            case Left(a) => Left(s"Error: ${a}")
            case Right(b) => Right(b * 2)
          }
        }
        assertEmpty(noError)
      }
    }
  }
}
