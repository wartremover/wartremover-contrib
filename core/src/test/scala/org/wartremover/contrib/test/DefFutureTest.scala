package org.wartremover
package contrib.test

import org.scalatest.funsuite.AnyFunSuite
import org.wartremover.contrib.warts.DefFuture
import org.wartremover.test.WartTestTraverser
import scala.concurrent.Future

class DefFutureTest extends AnyFunSuite with ResultAssertions {
  implicit val ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.global

  private def mk_fut() =
    Future(println("foo")).map(_ => 42)

  test("error if inferred typed Future is `val`") {
    val result = WartTestTraverser(DefFuture) {
      val fut = mk_fut()
    }
    assertError(result)(DefFuture.valErrorMsg("fut", "val"))
  }

  test("error if explicitly typed Future is `val`") {
    val result = WartTestTraverser(DefFuture) {
      val fut: Future[Int] = mk_fut()
    }
    assertError(result)(DefFuture.valErrorMsg("fut", "val"))
  }

  test("error if inferred typed Future is `lazy val`") {
    val result: WartTestTraverser.Result = WartTestTraverser(DefFuture) {
      lazy val fut = mk_fut()
    }
    assertError(result)(DefFuture.valErrorMsg("fut", "lazy val"))
  }

  test("error if explicitly typed Future is `lazy val`") {
    val result: WartTestTraverser.Result = WartTestTraverser(DefFuture) {
      lazy val fut: Future[Int] = mk_fut()
    }
    assertError(result)(DefFuture.valErrorMsg("fut", "lazy val"))
  }

  test("success if type is not Future") {
    val result = WartTestTraverser(DefFuture) {
      def fut = 42
    }
    assertEmpty(result)
  }

  test("success if Future is `def`") {
    val result = WartTestTraverser(DefFuture) {
      def fut = mk_fut()
    }
    assertEmpty(result)
  }

  test("can suppress warnings") {
    val result = WartTestTraverser(DefFuture) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.DefFuture"))
      val fut = mk_fut()
    }
    assertEmpty(result)
  }
}
