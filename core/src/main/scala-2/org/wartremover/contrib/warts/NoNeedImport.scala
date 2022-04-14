package org.wartremover
package contrib.warts

@deprecated("move to core https://github.com/wartremover/wartremover/commit/25b3a07a912c", "2.0.0")
object NoNeedImport extends WartTraverser {

  def apply(u: WartUniverse): u.Traverser =
    org.wartremover.warts.NoNeedImport(u)
}
