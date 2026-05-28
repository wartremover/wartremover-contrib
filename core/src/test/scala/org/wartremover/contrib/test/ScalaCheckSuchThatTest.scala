package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.ScalaCheckSuchThat
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite
import org.scalacheck.Gen

class ScalaCheckSuchThatTest extends AnyFunSuite with ResultAssertions {
  private val gen1: Gen[Int] = Gen.posNum[Int]
  private val f: Int => Boolean = _ => true

  test("filter") {
    val result = WartTestTraverser(ScalaCheckSuchThat) {
      gen1.filter(f)
    }
    assertError(result)("org.scalacheck.Gen.filter is disabled")
  }

  test("filterNot") {
    val result = WartTestTraverser(ScalaCheckSuchThat) {
      gen1.filterNot(f)
    }
    assertError(result)("org.scalacheck.Gen.filterNot is disabled")
  }

  test("withFilter") {
    val result = WartTestTraverser(ScalaCheckSuchThat) {
      gen1.withFilter(f)
    }
    assertError(result)("org.scalacheck.Gen.withFilter is disabled")
  }

  test("suchThat") {
    val result = WartTestTraverser(ScalaCheckSuchThat) {
      gen1.suchThat(f)
    }
    assertError(result)("org.scalacheck.Gen.suchThat is disabled")
  }

  test("obeys SuppressWarnings") {
    val result = WartTestTraverser(ScalaCheckSuchThat) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.ScalaCheckSuchThat"))
      def x(): Seq[Gen[Int]] = Seq(
        gen1.filter(f),
        gen1.filterNot(f),
        gen1.withFilter(f).map(_ + 1),
        gen1.suchThat(f),
      )
    }
    assertEmpty(result)
  }
}
