package org.wartremover.contrib

import sbt._
import sbt.Keys._
import wartremover.contrib.ContribWart
import wartremover.WartRemover
import wartremover.WartRemover.autoImport.wartremoverClasspaths

object ContribWarts extends AutoPlugin {

  object autoImport {
    val ContribWart = wartremover.contrib.ContribWart
  }

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = WartRemover

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    libraryDependencies += "org.wartremover" %% "wartremover-contrib" % ContribWart.ContribVersion$ % Provided,
    libraryDependencies ++= {
      // https://github.com/wartremover/wartremover/issues/106#issuecomment-51963190
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) =>
          Seq(compilerPlugin(
            "org.scalamacros" % "paradise" % wartremover.contrib.ContribWart.macroParadiseVersion cross CrossVersion.full))
        case _ =>
          Nil
      }
    },
    wartremoverClasspaths ++= {
      (dependencyClasspath in Compile).value.files
        .find(_.name.contains("wartremover-contrib"))
        .map(_.toURI.toString)
        .toList
    })
}
