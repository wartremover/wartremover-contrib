package org.wartremover
package contrib.test

import org.scalatest.FunSuite
import org.wartremover.contrib.warts.UnintendedLaziness
import org.wartremover.test.WartTestTraverser

import scala.collection.generic.CanBuildFrom
import scala.collection.parallel.{ Combiner, ParMap }
import scala.collection.{ GenIterable, GenMap, GenMapLike, GenSeq, GenSet, GenTraversable, GenTraversableOnce, immutable, mutable }
import scala.reflect.ClassTag

class UnintendedLazinessTest extends FunSuite with ResultAssertions {
  test("Can't call filterKeys on a map") {
    val map = Map.empty[String, Int]
    val result = WartTestTraverser(UnintendedLaziness) {
      map.filterKeys(_.isEmpty)
    }
    assertError(result)(UnintendedLaziness.errorForFilterKeys)
  }

  test("Can't call mapValues on a map") {
    val map = Map.empty[String, Int]
    val result = WartTestTraverser(UnintendedLaziness) {
      map.mapValues(_ + 1)
    }
    assertError(result)(UnintendedLaziness.errorForMapValues)
  }

  test("Can't call filterKeys on a new implementation of a map") {
    val newMap = new NewMap
    val result = WartTestTraverser(UnintendedLaziness) {
      newMap.filterKeys(_.isEmpty)
    }
    assertError(result)(UnintendedLaziness.errorForFilterKeys)
  }

  test("Can't call mapValues on a new implementation of a map") {
    val newMap = new NewMap
    val result = WartTestTraverser(UnintendedLaziness) {
      newMap.mapValues(_ + 1)
    }
    assertError(result)(UnintendedLaziness.errorForMapValues)
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

  class NewMap extends GenMapLike[String, Int, NewMap] {
    override def default(key: String): Int = ???

    override def get(key: String): Option[Int] = ???

    override def apply(key: String): Int = ???

    override def seq: collection.Map[String, Int] = ???

    override def +[V1 >: Int](kv: (String, V1)): GenMap[String, V1] = ???

    override def -(key: String): NewMap = ???

    override def getOrElse[V1 >: Int](key: String, default: => V1): V1 = ???

    override def contains(key: String): Boolean = ???

    override def isDefinedAt(key: String): Boolean = ???

    override def keySet: GenSet[String] = ???

    override def keys: GenIterable[String] = ???

    override def values: GenIterable[Int] = ???

    override def keysIterator: Iterator[String] = ???

    override def valuesIterator: Iterator[Int] = ???

    override def filterKeys(p: String => Boolean): GenMap[String, Int] = ???

    override def mapValues[W](f: Int => W): GenMap[String, W] = ???

    override def iterator: Iterator[(String, Int)] = ???

    override def sameElements[A1 >: (String, Int)](that: GenIterable[A1]): Boolean = ???

    override def zip[A1 >: (String, Int), B, That](that: GenIterable[B])(implicit bf: CanBuildFrom[NewMap, (A1, B), That]): That = ???

    override def zipWithIndex[A1 >: (String, Int), That](implicit bf: CanBuildFrom[NewMap, (A1, Int), That]): That = ???

    override def zipAll[B, A1 >: (String, Int), That](that: GenIterable[B], thisElem: A1, thatElem: B)(implicit bf: CanBuildFrom[NewMap, (A1, B), That]): That = ???

    override def repr: NewMap = ???

    override def size: Int = ???

    override def head: (String, Int) = ???

    override def headOption: Option[(String, Int)] = ???

    override def isTraversableAgain: Boolean = ???

    override def tail: NewMap = ???

    override def last: (String, Int) = ???

    override def lastOption: Option[(String, Int)] = ???

    override def init: NewMap = ???

    override def scan[B >: (String, Int), That](z: B)(op: (B, B) => B)(implicit cbf: CanBuildFrom[NewMap, B, That]): That = ???

    override def scanLeft[B, That](z: B)(op: (B, (String, Int)) => B)(implicit bf: CanBuildFrom[NewMap, B, That]): That = ???

    override def scanRight[B, That](z: B)(op: ((String, Int), B) => B)(implicit bf: CanBuildFrom[NewMap, B, That]): That = ???

    override def foreach[U](f: ((String, Int)) => U): Unit = ???

    override def map[B, That](f: ((String, Int)) => B)(implicit bf: CanBuildFrom[NewMap, B, That]): That = ???

    override def collect[B, That](pf: PartialFunction[(String, Int), B])(implicit bf: CanBuildFrom[NewMap, B, That]): That = ???

    override def flatMap[B, That](f: ((String, Int)) => GenTraversableOnce[B])(implicit bf: CanBuildFrom[NewMap, B, That]): That = ???

    override def ++[B >: (String, Int), That](that: GenTraversableOnce[B])(implicit bf: CanBuildFrom[NewMap, B, That]): That = ???

    override def filter(pred: ((String, Int)) => Boolean): NewMap = ???

    override def filterNot(pred: ((String, Int)) => Boolean): NewMap = ???

    override def partition(pred: ((String, Int)) => Boolean): (NewMap, NewMap) = ???

    override def groupBy[K](f: ((String, Int)) => K): GenMap[K, NewMap] = ???

    override def take(n: Int): NewMap = ???

    override def drop(n: Int): NewMap = ???

    override def slice(unc_from: Int, unc_until: Int): NewMap = ???

    override def splitAt(n: Int): (NewMap, NewMap) = ???

    override def takeWhile(pred: ((String, Int)) => Boolean): NewMap = ???

    override def span(pred: ((String, Int)) => Boolean): (NewMap, NewMap) = ???

    override def dropWhile(pred: ((String, Int)) => Boolean): NewMap = ???

    override def stringPrefix: String = ???

    override protected[this] def parCombiner: Combiner[(String, Int), ParMap[String, Int]] = ???

    override def hasDefiniteSize: Boolean = ???

    override def isEmpty: Boolean = ???

    override def nonEmpty: Boolean = ???

    override def reduce[A1 >: (String, Int)](op: (A1, A1) => A1): A1 = ???

    override def reduceOption[A1 >: (String, Int)](op: (A1, A1) => A1): Option[A1] = ???

    override def fold[A1 >: (String, Int)](z: A1)(op: (A1, A1) => A1): A1 = ???

    override def /:[B](z: B)(op: (B, (String, Int)) => B): B = ???

    override def :\[B](z: B)(op: ((String, Int), B) => B): B = ???

    override def foldLeft[B](z: B)(op: (B, (String, Int)) => B): B = ???

    override def foldRight[B](z: B)(op: ((String, Int), B) => B): B = ???

    override def aggregate[B](z: => B)(seqop: (B, (String, Int)) => B, combop: (B, B) => B): B = ???

    override def reduceRight[B >: (String, Int)](op: ((String, Int), B) => B): B = ???

    override def reduceLeftOption[B >: (String, Int)](op: (B, (String, Int)) => B): Option[B] = ???

    override def reduceRightOption[B >: (String, Int)](op: ((String, Int), B) => B): Option[B] = ???

    override def count(p: ((String, Int)) => Boolean): Int = ???

    override def sum[A1 >: (String, Int)](implicit num: Numeric[A1]): A1 = ???

    override def product[A1 >: (String, Int)](implicit num: Numeric[A1]): A1 = ???

    override def min[A1 >: (String, Int)](implicit ord: Ordering[A1]): (String, Int) = ???

    override def max[A1 >: (String, Int)](implicit ord: Ordering[A1]): (String, Int) = ???

    override def maxBy[B](f: ((String, Int)) => B)(implicit cmp: Ordering[B]): ((String, Int)) = ???

    override def minBy[B](f: ((String, Int)) => B)(implicit cmp: Ordering[B]): ((String, Int)) = ???

    override def forall(p: ((String, Int)) => Boolean): Boolean = ???

    override def exists(p: ((String, Int)) => Boolean): Boolean = ???

    override def find(p: ((String, Int)) => Boolean): Option[((String, Int))] = ???

    override def copyToArray[B >: (String, Int)](xs: Array[B]): Unit = ???

    override def copyToArray[B >: (String, Int)](xs: Array[B], start: Int): Unit = ???

    override def copyToArray[B >: (String, Int)](xs: Array[B], start: Int, len: Int): Unit = ???

    override def mkString(start: String, sep: String, end: String): String = ???

    override def mkString(sep: String): String = ???

    override def mkString: String = ???

    override def toArray[A1 >: (String, Int)](implicit evidence$1: ClassTag[A1]): Array[A1] = ???

    override def toList: List[(String, Int)] = ???

    override def toIndexedSeq: immutable.IndexedSeq[(String, Int)] = ???

    override def toStream: Stream[(String, Int)] = ???

    override def toIterator: Iterator[(String, Int)] = ???

    override def toBuffer[A1 >: (String, Int)]: mutable.Buffer[A1] = ???

    override def toTraversable: GenTraversable[(String, Int)] = ???

    override def toIterable: GenIterable[(String, Int)] = ???

    override def toSeq: GenSeq[(String, Int)] = ???

    override def toSet[A1 >: (String, Int)]: GenSet[A1] = ???

    override def toMap[K, V](implicit ev: (String, Int) <:< (K, V)): GenMap[K, V] = ???

    override def toVector: Vector[(String, Int)] = ???

    override def to[Col[_]](implicit cbf: CanBuildFrom[Nothing, (String, Int), Col[(String, Int)]]): Col[(String, Int)] = ???

    override def canEqual(that: Any): Boolean = ???
  }

}
