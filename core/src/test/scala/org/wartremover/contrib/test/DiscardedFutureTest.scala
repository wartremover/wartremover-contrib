package org.wartremover
package contrib.test

import org.scalatest.funsuite.AnyFunSuite
import org.wartremover.contrib.warts.DiscardedFuture
import org.wartremover.test.WartTestTraverser
import scala.concurrent.Future
import scala.util.Try

class DiscardedFutureTest extends AnyFunSuite with ResultAssertions {
  implicit val ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.global

  test("error if Future is a return type of anonymous partial function`") {
    val result = WartTestTraverser(DiscardedFuture) {
      val f = Future.successful(1)
      f.andThen { case _ =>
        f
      }
    }
    assertError(result)(DiscardedFuture.message)
  }

  test("error if Future is a return type of the value") {
    val result = WartTestTraverser(DiscardedFuture) {
      val f = Future.successful(1)
      val pf: PartialFunction[Try[Int], Future[String]] = { case _ =>
        Future.successful("")
      }
      f.andThen(pf)
    }
    assertError(result)(DiscardedFuture.message)
  }

  test("success if non-Future is a type of return value") {
    val result = WartTestTraverser(DiscardedFuture) {
      val f = Future.successful(1)
      f.andThen { case _ =>
        1
      }
    }
    assertEmpty(result)
  }

  test("can suppress warnings") {
    val result = WartTestTraverser(DiscardedFuture) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.DiscardedFuture"))
      def m() = {
        val f = Future.successful(1)
        f.andThen { case _ =>
          f
        }
      }
    }
    assertEmpty(result)
  }
}
