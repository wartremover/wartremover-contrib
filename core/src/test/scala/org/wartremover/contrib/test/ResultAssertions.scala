package org.wartremover
package contrib.test

import munit.FunSuite
import org.wartremover.test.WartTestTraverser

trait ResultAssertions extends FunSuite {

  def assertEmpty(result: WartTestTraverser.Result) = {
    assert(result.errors == Nil)
    assert(result.warnings == Nil)
  }

  def assertError(result: WartTestTraverser.Result)(message: String) = assertErrors(result)(message, 1)

  def assertErrors(result: WartTestTraverser.Result)(message: String, times: Int) = {
    assert(result.errors.map(skipTraverserPrefix) == List.fill(times)(message))
    assert(result.warnings.map(skipTraverserPrefix) == Nil)
  }

  def assertWarnings(result: WartTestTraverser.Result)(message: String, times: Int) = {
    assert(result.errors.map(skipTraverserPrefix) == Nil)
    assert(result.warnings.map(skipTraverserPrefix) == List.fill(times)(message))
  }

  private val messageFormat = """\[wartremover:\S+\] ([\s\S]+)""".r

  private def skipTraverserPrefix(msg: String) = msg match {
    case messageFormat(rest) => rest
    case s => s
  }
}
