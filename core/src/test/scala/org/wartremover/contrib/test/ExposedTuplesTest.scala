package org.wartremover
package contrib.test

import org.wartremover.contrib.warts.ExposedTuples
import org.wartremover.test.WartTestTraverser
import org.scalatest.funsuite.AnyFunSuite

class ExposedTuplesTest extends AnyFunSuite with ResultAssertions {

  test("can't expose a tuple from a public method") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar1(): (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public method in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        def bar2(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected method in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected def bar3(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private method in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private def bar4(): (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private method for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] def bar5(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a method inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar6(): Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a method if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar7(): Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public method as a parameter") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar8(baz: (Int, String)) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public method as a parameter in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        def bar9(baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected method as a parameter in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected def bar10(baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private method as a parameter in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private def bar11(baz: (Int, String)) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private method as a parameter for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] def bar12(baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a method as a parameter inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar13(baz: Seq[(Int, String)]) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a method as a parameter if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar14(baz: Map[Int, String]) = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      val bar15: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        val bar16: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected val bar17: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private val bar18: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private value for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] val bar19: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a value inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      val bar20: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a value if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      val bar21: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public variable") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      var bar22: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public variable in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        var bar23: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected variable in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected var bar24: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private variable in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private var bar25: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private variable for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] var bar26: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a variable inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      var bar27: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a variable if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      var bar28: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public lazy value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      lazy val bar29: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public lazy value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        lazy val bar30: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected lazy value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected lazy val bar31: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private lazy value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private lazy val bar32: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private lazy value for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] lazy val bar33: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a lazy value inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      lazy val bar34: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a lazy value if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      lazy val bar35: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public implicit method") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit def bar36(): (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public implicit method in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        implicit def bar37(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected implicit method in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected implicit def bar38(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private implicit method in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private implicit def bar39(): (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private implicit method for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] implicit def bar40(): (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from an implicit method inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit def bar41(): Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from an implicit method if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit def bar42(): Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public implicit value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit val bar43: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public implicit value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        implicit val bar44: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected implicit value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected implicit val bar45: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private implicit value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private implicit val bar46: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private implicit value for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] implicit val bar47: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from an implicit value inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit val bar48: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from an implicit value if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit val bar49: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public implicit variable") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit var bar50: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public implicit variable in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        implicit var bar51: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected implicit variable in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected implicit var bar52: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private implicit variable in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private implicit var bar53: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private implicit variable for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] implicit var bar54: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from an implicit variable inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit var bar55: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from an implicit variable if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      implicit var bar56: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public implicit lazy value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      lazy implicit val bar57: (Int, String) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public implicit lazy value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        lazy implicit val bar58: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected implicit lazy value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected lazy implicit val bar59: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private implicit lazy value in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private lazy implicit val bar60: (Int, String) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private implicit lazy value for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] lazy implicit val bar61: (Int, String) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from an implicit lazy value inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      lazy implicit val bar62: Seq[(Int, String)] = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from an implicit lazy value if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      lazy implicit val bar63: Map[Int, String] = ???
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a public method as an implicit parameter") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar64(implicit baz: (Int, String)) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a public method as an implicit parameter in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        def bar65(implicit baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected method as an implicit parameter in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        protected def bar66(implicit baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private method as an implicit parameter in a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private def bar67(implicit baz: (Int, String)) = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple from a private method as an implicit parameter for a scope") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        private[test] def bar68(implicit baz: (Int, String)) = ???
      }
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a method as an implicit parameter inside another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar69(implicit baz: Seq[(Int, String)]) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a method as an implicit parameter if it's the base type of another type") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def bar70(implicit baz: Map[Int, String]) = ???
    }
    assertEmpty(result)
  }

  test("can expose a tuple from the unapply method of a case class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      case class Foo(a: Int, b: Int)
    }
    assertEmpty(result)
  }

  test("can expose a tuple from a custom unapply method") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
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

  test("can't expose a tuple from a implicit class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      object X {
        implicit class Foo(tuple: (Int, String))
      }
    }
    assertError(result.copy(errors = result.errors.distinct))(ExposedTuples.message)
  }

  test("can't expose a tuple from a public constructor") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo(tuple: (Int, String))
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple from a protected constructor") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo protected (tuple: (Int, String))
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a private constructor") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo private (tuple: (Int, String))
    }
    assertEmpty(result)
  }

  test("can expose a tuple from a private scoped constructor") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo private[test] (tuple: (Int, String))
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can expose a tuple from a local def") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        def bar71(): (String, Int) = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local def parameter") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        def bar72(baz: (String, Int)) = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple from a local def inside a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        def foo: Unit = {
          def bar73(): (String, Int) = ???
        }
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local def parameter inside a class") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      class Foo {
        def foo: Unit = {
          def bar74(baz: (String, Int)) = ???
        }
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        val bar75: (String, Int) = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local value lambda") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        val bar76: Unit => (String, Int) = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local value lambda's parameter") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        val bar77: ((String, Int)) => Unit = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local variable") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        var bar75b: (String, Int) = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local variable lambda") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        var bar76b: Unit => (String, Int) = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local variable lambda's parameter") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        var bar77b: ((String, Int)) => Unit = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local lazy value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        lazy val bar75c: (String, Int) = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local lazy value lambda") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        lazy val bar76c: Unit => (String, Int) = ???
      }
    }
    assertEmpty(result)
  }

  test("can expose a tuple as a local lazy value lambda's parameter") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      def foo: Unit = {
        lazy val bar77c: ((String, Int)) => Unit = ???
      }
    }
    assertEmpty(result)
  }

  test("can't expose a tuple as a value lambda") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      val bar78: ((String, Int)) => Unit = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple as a variable lambda") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      var bar79: ((String, Int)) => Unit = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple as a lazy value lambda") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      lazy val bar80: ((String, Int)) => Unit = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple as a value lambda's return value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      val bar81: Unit => (String, Int) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple as a variable lambda's return value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      var bar82: Unit => (String, Int) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("can't expose a tuple as a lazy value lambda's return value") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      lazy val bar83: Unit => (String, Int) = ???
    }
    assertError(result)(ExposedTuples.message)
  }

  test("obeys SuppressWarnings") {
    val result: WartTestTraverser.Result = WartTestTraverser(ExposedTuples) {
      @SuppressWarnings(Array("org.wartremover.contrib.warts.ExposedTuples"))
      def bar(): (Int, String) = ???

      object X {
        @SuppressWarnings(Array("org.wartremover.contrib.warts.ExposedTuples"))
        implicit class TupleOps[A, B](tuple: (A, B))
      }
    }
    assertEmpty(result)
  }
}
