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

  override lazy val projectSettings: Seq[Setting[?]] = Seq(
    wartremoverDependencies += {
      sbtBinaryVersion.value match {
        case "2" =>
          // https://github.com/sbt/sbt/issues/9132
          wartremoverCrossVersion.value match {
            case _: CrossVersion.Full =>
              "org.wartremover" % s"wartremover-contrib_${scalaVersion.value}" % ContribWart.ContribVersion
            case _: CrossVersion.Binary =>
              "org.wartremover" % s"wartremover-contrib_${scalaBinaryVersion.value}" % ContribWart.ContribVersion
            case _ =>
              ("org.wartremover" %% "wartremover-contrib" % ContribWart.ContribVersion)
                .cross(wartremoverCrossVersion.value)
          }
        case _ =>
          ("org.wartremover" %% "wartremover-contrib" % ContribWart.ContribVersion).cross(wartremoverCrossVersion.value)
      }
    }
  )
}
