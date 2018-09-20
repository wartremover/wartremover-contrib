package org.wartremover
package contrib.test

import org.scalatest.FunSuite
import org.wartremover.contrib.warts.UnintendedLaziness
import org.wartremover.test.WartTestTraverser

class UnintendedLazinessTest extends FunSuite with ResultAssertions {
  private lazy val isScala213: Boolean = try {
    ClassLoader.getSystemClassLoader.loadClass("scala.collection.GenMapLike")
    false
  } catch {
    case _: ClassNotFoundException => true
  }

  test("Can't call filterKeys on a map") {
    val map = Map.empty[String, Int]
    val result = WartTestTraverser(UnintendedLaziness) {
      map.filterKeys(_.isEmpty)
    }

    if (isScala213) {
      assertEmpty(result)
    } else {
      assertError(result)(UnintendedLaziness.errorForFilterKeys)
    }
  }

  test("Can't call mapValues on a map") {
    val map = Map.empty[String, Int]
    val result = WartTestTraverser(UnintendedLaziness) {
      map.mapValues(_ + 1)
    }

    if (isScala213) {
      assertEmpty(result)
    } else {
      assertError(result)(UnintendedLaziness.errorForMapValues)
    }
  }

  test("Can call other methods on a map") {
    val map = Map.empty[String, Int]
    val result = WartTestTraverser(UnintendedLaziness) {
      map.filter { case (key, _) => key.isEmpty }
      map.map { case (key, value) => key -> (value + 1) }
    }
    assertEmpty(result)
  }

  test("Can call filterKeys on anything that's not a map") {
    val notAMap = new {
      def filterKeys(p: String => Boolean): Map[String, Int] = ???
    }

    val result = WartTestTraverser(UnintendedLaziness) {
      notAMap.filterKeys(_.isEmpty)
    }
    assertEmpty(result)
  }

  test("Can't call mapValues on anything that's not a map") {
    val notAMap = new {
      def mapValues[W](f: Int => W): Map[String, W] = ???
    }

    val result = WartTestTraverser(UnintendedLaziness) {
      notAMap.mapValues(_ + 1)
    }
    assertEmpty(result)
  }

  test("obeys SuppressWarnings") {
    val map = Map.empty[String, Int]

    val result = WartTestTraverser(UnintendedLaziness) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.UnintendedLaziness"))
      val _ = {
        map.filterKeys(_.isEmpty)
        map.mapValues(_ + 1)
      }
    }
    assertEmpty(result)
  }

}
