package org.wartremover
package contrib.warts

object NoNeedImport extends WartTraverser {

  /**
   * @param existsNameToWildCard  Exists `import aaa.bbb.{ ccc => _ }` or not
   * @param existsWildCard        Exists `import aaa.bbb.{ _ }` or not
   */
  case class ImportTypeContainer(
    existsNameToWildCard: Boolean,
    existsWildCard: Boolean)

  def apply(u: WartUniverse): u.Traverser = {
    import u.universe._

    val init: ImportTypeContainer =
      ImportTypeContainer(
        existsNameToWildCard = false,
        existsWildCard = false)

    new Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          case t if hasWartAnnotation(u)(t) =>
          // Ignore trees marked by SuppressWarnings

          case Import(_, iss) =>
            iss.foldLeft(init) {
              case (acc, ImportSelector(termNames.WILDCARD, _, _, _)) =>
                // If `import aaa.bbb.{ _ => ccc }` or `import aaa.bbb.{ _ => _ }`
                // would be written, it should be the same meaning of
                // `import aaa.bbb._` so we don't need to care the 3rd parameter of the `ImportSelector`
                acc.copy(
                  existsWildCard = true)
              case (acc, ImportSelector(_, _, termNames.WILDCARD, _)) =>
                acc.copy(
                  existsNameToWildCard = true)
              case (acc, _) =>
                acc
            } match {
              case ImportTypeContainer(true, true) =>
              // In this case, there are `import aaa.bbb.{ ccc => _, ddd, _ }`.
              // It means that all should be imported except `ccc`.
              case ImportTypeContainer(true, false) =>
                // In case `import aaa.bbb.{ ccc => _, ddd }`,
                // a programmer could remove `ccc => _`.
                error(u)(
                  tree.pos,
                  "Import into the wildcard(`something => _`) is meaningless. Remove it.")
              case ImportTypeContainer(false, true) =>
                if (iss.size >= 2) {
                  error(u)(
                    tree.pos,
                    "The wildcard import exists. Remove other explicitly names of the `import`.")
                }
              case _ =>
            }

          case _ =>
        }
        super.traverse(tree)
      }
    }
  }
}
