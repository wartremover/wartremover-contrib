package org.wartremover.contrib.test

import org.scalatest.funsuite.AnyFunSuite
import org.wartremover.InspectParam
import org.wartremover.WartRemoverInspector
import sbt.io.IO
import scala.quoted.Quotes
import scala.tasty.inspector.Inspector
import scala.tasty.inspector.Tasty
import scala.tasty.inspector.TastyInspector

class WartRemoverInspectorTest extends AnyFunSuite {
  extension (groupId: String) {
    def %(artifactId: String): coursier.core.Module =
      coursier.core.Module(
        coursier.core.Organization(groupId),
        coursier.core.ModuleName(artifactId),
        Map.empty
      )

    def %%(artifactId: String): coursier.core.Module =
      %(artifactId + "_3")
  }

  extension (module: coursier.core.Module) {
    def %(version: String): coursier.core.Dependency =
      coursier.core.Dependency(module, version)
  }

  private val inspector = new WartRemoverInspector
  private def packagePrefix = "org.wartremover.contrib.warts."

  // TODO org.wartremover.contrib.warts.ExposedTuples
  private val allWarts: List[String] = List(
    org.wartremover.contrib.warts.Apply,
    org.wartremover.contrib.warts.DiscardedFuture,
    org.wartremover.contrib.warts.MissingOverride,
    org.wartremover.contrib.warts.OldTime,
    org.wartremover.contrib.warts.RefinedClasstag,
    org.wartremover.contrib.warts.SealedCaseClass,
    org.wartremover.contrib.warts.SomeApply,
    org.wartremover.contrib.warts.SymbolicName,
    org.wartremover.contrib.warts.UnintendedLaziness,
    org.wartremover.contrib.warts.UnsafeInheritance,
  ).map(_.fullName)

  private def inspectLibrary(module: coursier.core.Dependency): Map[String, Map[String, Int]] = {
    val jars = coursier.Fetch().addDependencies(module).run()
    jars.map { jar =>
      println("start " + jar)
      val result = IO.withTemporaryDirectory { dir =>
        val tastyFiles = IO.unzip(jar, dir, _ endsWith ".tasty").map(_.getAbsolutePath).toList
        println("tasty files count = " + tastyFiles.size)
        val param = InspectParam(
          tastyFiles = tastyFiles,
          dependenciesClasspath = jars.map(_.getAbsolutePath).toList,
          wartClasspath = Nil,
          errorWarts = Nil,
          warningWarts = allWarts,
          exclude = Nil,
          failIfWartLoadError = true,
          outputStandardReporter = true
        )
        inspector.run(param)
      }
      jar.getName -> result.warnings.map(_.wart.replace(packagePrefix, "")).groupBy(identity).map { case (k, v) =>
        k -> v.size
      }
    }.toMap
  }

  private def isNewScala: Boolean = {
    val path = classOf[Quotes].getProtectionDomain.getCodeSource.getLocation.getPath
    !Seq("3.1.1", "3.1.2").exists(path contains _)
  }

  test("cats") {
    if (isNewScala) {
      val result = inspectLibrary("org.typelevel" %% "cats-core" % "2.7.0")
      assert(
        result("cats-kernel_3-2.7.0.jar") === Map(
          "SomeApply" -> 27,
          "MissingOverride" -> 1154,
          "UnsafeInheritance" -> 1152,
          "Apply" -> 1,
        )
      )
      assert(
        result("scala3-library_3-3.0.2.jar") === Map(
          "SomeApply" -> 87,
          "MissingOverride" -> 113,
          "UnsafeInheritance" -> 119,
          "Apply" -> 126,
        )
      )
      assert(
        result("cats-core_3-2.7.0.jar") === Map(
          "SomeApply" -> 141,
          "MissingOverride" -> 1290,
          "UnsafeInheritance" -> 1342,
          "Apply" -> 116,
        )
      )
      assert(result("scala-library-2.13.6.jar") === Map.empty)
      assert(result("simulacrum-scalafix-annotations_3-0.5.4.jar") === Map.empty)
      assert(result.size === 5)
    } else {
      // avoid old scala versions due to Scala 3 bug
      pending
    }
  }
}
