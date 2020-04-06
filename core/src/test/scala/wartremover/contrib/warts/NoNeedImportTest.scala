package wartremover.contrib.warts

import org.scalatest.funsuite.AnyFunSuite
import org.wartremover.contrib.test.ResultAssertions
import org.wartremover.contrib.warts.NoNeedImport
import org.wartremover.test.WartTestTraverser

class NoNeedImportTest extends AnyFunSuite with ResultAssertions {
  test("`import scala.util.{ Try, _ }` is disabled") {
    val result = WartTestTraverser(NoNeedImport) {
      import scala.util.{ Try, _ }
    }
    assertError(result)("The wildcard import exists. Remove other explicitly names of the `import`.")
  }
  test("`import scala.util.{ Try => _ }` is disabled") {
    val result = WartTestTraverser(NoNeedImport) {
      import scala.util.{ Try => _ }
    }
    assertError(result)("Import into the wildcard(`something => _`) is meaningless. Remove it.")
  }
  test("`import scala.util.{ Try => _ , _ }` can be used") {
    val result = WartTestTraverser(NoNeedImport) {
      import scala.util.{ Try => _, _ }
    }
    assertEmpty(result)
  }
  test("`import scala.util._` can be used") {
    val result = WartTestTraverser(NoNeedImport) {
      import scala.util._
      import scala.util.{ _ }
    }
    assertEmpty(result)
  }
}
