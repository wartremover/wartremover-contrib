package org.wartremover.contrib

import sbt._
import sbt.Keys._
import wartremover.contrib.ContribWart
import wartremover.WartRemover
import wartremover.WartRemover.autoImport.wartremoverDependencies
import wartremover.WartRemover.autoImport.wartremoverCrossVersion

object ContribWarts extends AutoPlugin {

  object autoImport {
    val ContribWart = wartremover.contrib.ContribWart
  }

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = WartRemover

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    wartremoverDependencies += "org.wartremover" %% "wartremover-contrib" % ContribWart.ContribVersion cross wartremoverCrossVersion.value
  )
}
