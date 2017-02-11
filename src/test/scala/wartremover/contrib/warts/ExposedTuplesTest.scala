package org.wartremover.contrib.warts

import org.scalatest.FunSuite
import org.wartremover.contrib.test.ResultAssertions
import org.wartremover.test.WartTestTraverser

class ExposedTuplesTest extends FunSuite with ResultAssertions {

  test("can't expose a tuple from a public method") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar1(): (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public method in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        def bar2(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected method in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected def bar3(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private method in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private def bar4(): (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private method for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] def bar5(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a method inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar6(): Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a method if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar7(): Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public method as a parameter") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar8(baz: (Int, String)) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public method as a parameter in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        def bar9(baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected method as a parameter in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected def bar10(baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private method as a parameter in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private def bar11(baz: (Int, String)) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private method as a parameter for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] def bar12(baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a method as a parameter inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar13(baz: Seq[(Int, String)]) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a method as a parameter if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar14(baz: Map[Int, String]) = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public value") {
    val result = WartTestTraverser(ExposedTuples) {
      val bar15: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        val bar16: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected val bar17: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private val bar18: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private value for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] val bar19: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a value inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      val bar20: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a value if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      val bar21: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public variable") {
    val result = WartTestTraverser(ExposedTuples) {
      var bar22: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public variable in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        var bar23: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected variable in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected var bar24: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private variable in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private var bar25: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private variable for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] var bar26: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a variable inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      var bar27: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a variable if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      var bar28: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public lazy value") {
    val result = WartTestTraverser(ExposedTuples) {
      lazy val bar29: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public lazy value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        lazy val bar30: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected lazy value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected lazy val bar31: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private lazy value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private lazy val bar32: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private lazy value for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] lazy val bar33: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a lazy value inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      lazy val bar34: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a lazy value if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      lazy val bar35: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public implicit method") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit def bar36(): (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public implicit method in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        implicit def bar37(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected implicit method in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected implicit def bar38(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private implicit method in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private implicit def bar39(): (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private implicit method for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] implicit def bar40(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from an implicit method inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit def bar41(): Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from an implicit method if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit def bar42(): Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public implicit value") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit val bar43: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public implicit value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        implicit val bar44: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected implicit value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected implicit val bar45: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private implicit value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private implicit val bar46: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private implicit value for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] implicit val bar47: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from an implicit value inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit val bar48: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from an implicit value if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit val bar49: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public implicit variable") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit var bar50: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public implicit variable in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        implicit var bar51: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected implicit variable in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected implicit var bar52: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private implicit variable in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private implicit var bar53: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private implicit variable for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] implicit var bar54: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from an implicit variable inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit var bar55: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from an implicit variable if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      implicit var bar56: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public implicit lazy value") {
    val result = WartTestTraverser(ExposedTuples) {
      lazy implicit val bar57: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public implicit lazy value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        lazy implicit val bar58: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected implicit lazy value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected lazy implicit val bar59: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private implicit lazy value in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private lazy implicit val bar60: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private implicit lazy value for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] lazy implicit val bar61: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from an implicit lazy value inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      lazy implicit val bar62: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from an implicit lazy value if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      lazy implicit val bar63: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public method as an implicit parameter") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar64(implicit baz: (Int, String)) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public method as an implicit parameter in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        def bar65(implicit baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected method as an implicit parameter in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected def bar66(implicit baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private method as an implicit parameter in a class") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private def bar67(implicit baz: (Int, String)) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private method as an implicit parameter for a scope") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[warts] def bar68(implicit baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a method as an implicit parameter inside another type") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar69(implicit baz: Seq[(Int, String)]) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a method as an implicit parameter if it's the base type of another type") {
    val result = WartTestTraverser(ExposedTuples) {
      def bar70(implicit baz: Map[Int, String]) = ???
    }
    assertEmpty(result)
  }

  test("can expose a tuple from the unapply method of a case class") {
    val result = WartTestTraverser(ExposedTuples) {
      case class Foo(a: Int, b: Int)
    }
    assertEmpty(result)
  }

  test("can expose a tuple from a custom unapply method") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo(val a: Int, val b: Int)

      object Foo {
        def unapply(foo: Foo): Option[(Int, Int)] = Some((foo.a, foo.b))
      }

      // Testing to make sure unapply is properly defined
      val foo = new Foo(1, 2)
      val Foo(a, b) = foo
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public constructor") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo(tuple: (Int, String))
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected constructor") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo protected (tuple: (Int, String))
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private constructor") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo private (tuple: (Int, String))
    }
    assertEmpty(result)
  }

  test("can expose a tuple from a private scoped constructor") {
    val result = WartTestTraverser(ExposedTuples) {
      class Foo private[warts] (tuple: (Int, String))
    }
    assertError(result)(ExposedTuples.message)
  }

  test("obeys SuppressWarnings") {
    val result = WartTestTraverser(ExposedTuples) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.ExposedTuples"))
      def bar(): (Int, String) = ???
    }
    assertEmpty(result)
  }
}
