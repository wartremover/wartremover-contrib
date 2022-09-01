# wartremover-contrib

[![Maven Central](https://img.shields.io/maven-central/v/org.wartremover/wartremover-contrib_2.13?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.wartremover%22%20AND%20a:%22sbt-wartremover-contrib%22)

A selection of additional warts for wartremover managed by the community.

## Usage

Add the following to your `project/plugins.sbt`:

```scala
addSbtPlugin("org.wartremover" % "sbt-wartremover-contrib" % "2.0.1")
```

```scala
// In build.sbt
wartremoverErrors += ContribWart.OldTime // Or whichever warts you want to add
```

To use `wartremover-contrib` with other build managers, please refer to [here](other_ways.md).

## Warts

Here is a list of warts under the `org.wartremover.contrib.warts` package.

### Apply

`apply` slightly reduces amount of code, but makes code much less readable, and in conjunction with parenless methods can lead to bugs.

`list.toSet(true)`

A short name can make code more meaningful:

`list.toSet.contains(true)`.

```scala
class C {
  def apply(...) = ... // Won't compile: apply is disabled
}
object C {
  def apply() = new C // Compiles: object's apply is enabled
}
```

### ExposedTuples

Tuples are described not by their semantic meaning, but by their types alone, which requires users of your API to either create that meaning themselves using unapply or to use the ugly _1, _2, ... accessors.

Public API should refrain from exposing tuples and should instead consider using custom case classes to add semantic meaning.

```scala
// Won't compile:
// | Avoid using tuples in public interfaces, as they only supply type information.
// | Consider using a custom case class to add semantic meaning.
def badFoo(customerTotal: (String, Long)) = {
  // Code
}
```
```scala
// Custom case class with added semantic meaning
final case class CustomerAccount(customerId: String, accountTotal: Long)

// Will compile
def goodFoo(customerTotal: CustomerAccount) = {
  // Code
}
```

### MissingOverride

Though `override` may be optional, it is safer to add it every time.
Consider the following code:

```scala
trait T {
  def f1(): Unit
}

class C extends T {
  def f1() = ...
  def f2() = ...
}
```

Renaming `T.f1` to `T.f2` leads to dead code and unexpected behavior in `C`.

It is advised to use this rule with `UnsafeInheritance` to avoid default implementation override:

```scala
trait T {
  def f1(): Unit = ...
}
```

### NoNeedForMonad

Sometimes an additional power of `Monad` is not needed, and
`Applicative` is enough. This issues a warning in such cases
(not an error, since using a `Monad` instance might still be a conscious decision)

```scala
scala> for {
     | x <- List(1,2,3)
     | y <- List(2,3,4)
     | } yield x * y
<console>:19: warning: No need for Monad here (Applicative should suffice).
 > "If the extra power provided by Monad isn’t needed, it’s usually a good idea to use Applicative instead."
 Typeclassopedia (https://wiki.haskell.org/Typeclassopedia)
 Apart from a cleaner code, using Applicatives instead of Monads can in general case result in a more parallel code.
 For more context, please refer to the aforementioned Typeclassopedia, http://comonad.com/reader/2012/abstracting-with-applicatives/, or http://www.serpentine.com/blog/2008/02/06/the-basics-of-applicative-functors-put-to-practical-work/
              x <- List(1,2,3)
                ^
res0: List[Int] = List(2, 3, 4, 4, 6, 8, 6, 9, 12)

scala> for {
     | x <- List(1,2,3)
     | y <- x to 3
     | } yield x * y
res1: List[Int] = List(1, 2, 3, 4, 6, 9)
```

### OldTime

Forbids use of deprecated time APIs in favor of the [Java 8 Time API](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html).

Disabled types:

* `java.util.Date`
* `java.util.Calendar`
* `java.util.GregorianCalendar`
* `java.util.TimeZone`
* `java.text.DateFormat`
* `java.text.SimpleDateFormat`
* `org.joda.time._`

### SealedCaseClass

Reports an error/warning when a sealed case class is seen. As per FinalCaseClass wart, case classes
should always be `final`. And, in Scala combining `final` and `sealed` together is not allowed.

### SomeApply

`Some.apply` may break typing in two ways: First, when it is used with a null value, creating an instance of `Some(null)` instead of the (usually) expected `None`, and; Second, when it causes type inference to infer `Some[T]` instead of the (usually) expected `Option[T]`. Use `Option.apply` instead, to cover both cases.

```scala
def someOfNull(foo: String) = {
  // Won't compile: Some.apply is disabled - use Option.apply instead
  val expectedSafeFoo: Option[String] = Some(foo) // If foo == null, Some(null)
  val actualSafeFoo: Option[String] = Option(foo) // If foo == null, None
}
```

```scala
def typeInference() = {
  // Won't compile: Some.apply is disabled - use Option.apply instead
  val maybeFoo = Some("bar") // maybeFoo has type Some[String], not Option[String]...
  
  // ...so the following code would not have compiled
  maybeFoo match {
    case Some(value) => // ...
    case None => // ...
  }
}
```

### SymbolicName

Symbolic names don't affect program correctness directly, however this language feature makes it harder to reason about the code, and that leads to bugs.

As a general rule, symbolic names have two valid use-cases: domain-specific languages, logically mathematical operations. Otherwise they can be replaced by normal readable names.

A name is considered symbolic if the number of characters that aren't letters or underscore is greater than 2.

```scala
// Won't compile: Symbolic name is disabled
def :+:(): Unit = {}
```

### UnintendedLaziness

The `mapValues` and `filterKeys` methods of maps implicitly turn a strictly evaluated collection into a lazily evaluated one.
This has been [the subject of many debates](https://issues.scala-lang.org/browse/SI-4776) and will be fixed in the new collections library in Scala 2.13, but until then should be avoided.

You should instead consider using the explicit call to the `view` or `toStream` methods. 

```scala
val map: Map[Int, Int] = ???

// Won't compile
val positivesLazyMap = map.filterKeys(_ > 0)

// Won't compile
val incrementedLazyMap = map.mapValues(_ + 1)
```

### UnsafeInheritance

Overriding method implementation can break parent's contract.
```scala
trait T {
  // Won't compile: Method must be final or abstract
  def positive = 1
}
class C extends T {
  override def positive = -1
}
```

### DiscardedFuture

The `andThen` method receives a side-effecting callback, whose return value are discarded.
This is confusing, because it is different from Function types' `andThen` which compose two instances of functions (e.g. `f andThen g`).
The `flatMap` method, which can chain the result to other `Future`, may be more appropriate than `andThen`.

```scala
val f: Future[Int] = fooAsync()

// Won't compile
f.andThen { 
  case i if i > 100 => Future.successful(0)
  case _ => Future.succesful(42)
}

// Will compile
f.andThen { 
  case i if i > 100 => println("side-effect")
  case _ => println("other side-effect")
}
```
