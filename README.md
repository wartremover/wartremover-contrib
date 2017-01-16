# wartremover-contrib
Additional warts for wartremover.

## Warts

### NoNeedForMonad

Here is a list of warts under the `org.wartremover.contrib.warts` package.

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
