# wartremover-contrib
Additional warts for wartremover.

## Usage

Add the following to your `project/plugins.sbt`:

```scala
addSbtPlugin("org.wartremover" % "sbt-wartremover-contrib" % "1.0.0")
```

To use `wartremover-contrib` with other build managers, please refer to [here](other_ways.md).

## Warts

Here is a list of warts under the `org.wartremover.contrib.warts` package.

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
 Typeclassopedia (http://www.haskell.org/haskellwiki/Typeclassopedia)
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
