package org.wartremover.contrib.test

import munit.FunSuite
import org.wartremover.InspectParam
import org.wartremover.WartRemoverInspector
import org.wartremover.WartTraverser
import sbt.io.IO
import scala.concurrent.duration.*
import scala.quoted.Quotes
import scala.tasty.inspector.Inspector
import scala.tasty.inspector.Tasty
import scala.tasty.inspector.TastyInspector

class WartRemoverInspectorTest extends FunSuite {
  override val munitTimeout: Duration = 150.seconds

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
      coursier.core.Dependency(
        module,
        coursier.version.VersionConstraint(version)
      )
  }

  private val inspector = new WartRemoverInspector
  private def packagePrefix = "org.wartremover.contrib.warts."

  private val allWarts: List[String] = {
    val exclude = Set[Class[?]](
      classOf[org.wartremover.contrib.warts.ExposedTuples.type]
    )
    val suffix = ".class"
    val sample = org.wartremover.contrib.warts.Apply.getClass
    val values = scala.io.Source
      .fromInputStream(
        sample.getProtectionDomain.getClassLoader.getResourceAsStream(
          sample.getPackage.getName.replace('.', '/')
        )
      )
      .getLines()
      .filter(_.endsWith(suffix))
      .map(_.dropRight(suffix.length))
      .map { className =>
        Class.forName(packagePrefix + className)
      }
      .filterNot(exclude)
      .filter { clazz =>
        !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers)
      }
      .filter { clazz =>
        Iterator
          .unfold[Class[?], Class[?]](clazz)(c => Option(c).map(x => c -> x.getSuperclass))
          .exists(_ == classOf[WartTraverser])
      }
      .map(_.getName.stripSuffix(scala.reflect.NameTransformer.MODULE_SUFFIX_STRING))
      .toList
    assert(values.size == 13)
    values
  }

  private def inspectLibrary(module: coursier.core.Dependency): Map[String, Map[String, Int]] = {
    val jars = coursier.Fetch().addDependencies(module).run()
    jars
      .filterNot(_.getAbsolutePath.contains("/scala3-library_3/"))
      .map { jar =>
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
      }
      .toMap
  }

  test("cats") {
    val result = inspectLibrary("org.typelevel" %% "cats-core" % "2.10.0")
    assert(
      result("cats-kernel_3-2.10.0.jar") == Map(
        "SomeApply" -> 29,
        "MissingOverride" -> 379,
        "UnsafeInheritance" -> 1160,
        "Apply" -> 3,
      )
    )
    assert(result("cats-core_3-2.10.0.jar") == Map.empty)
    assert(result("scala-library-2.13.10.jar") == Map.empty)
    assert(result.size == 3)
  }
}
