import ReleaseTransformations._
import sbt.internal.ProjectMatrix

Global / onChangedBuildSource := ReloadOnSourceChanges

val wartremoverVersion = "3.4.1"

val scala212Versions = Seq(
  "2.12.18",
  "2.12.19",
  "2.12.20",
)
val scala213Versions = Seq(
  "2.13.14",
  "2.13.15",
  "2.13.16",
  "2.13.17",
)
val scala3Versions = Seq(
  "3.3.3",
  "3.3.4",
  "3.3.5",
  "3.3.6",
  "3.3.7",
  "3.4.3",
  "3.5.2",
  "3.6.3",
  "3.6.4",
  "3.7.0",
  "3.7.1",
  "3.7.2",
  "3.7.3",
)

def latest(versions: Seq[String]) = {
  val prefix = versions.head.split('.').init.mkString("", ".", ".")
  assert(versions.forall(_ startsWith prefix))
  prefix + versions.map(_.drop(prefix.length).toLong).max
}

val scala212Latest = latest(scala212Versions)
val scala213Latest = latest(scala213Versions)
val scala3Latest = scala3Versions.filterNot(_ contains "-RC").filter(_ startsWith "3.3.").last // TODO more better way

lazy val commonSettings = Seq(
  organization := "org.wartremover",
  licenses := Seq(
    "The Apache Software License, Version 2.0" ->
      url("https://www.apache.org/licenses/LICENSE-2.0.txt")
  ),
  scalacOptions ++= Seq(
    "-deprecation"
  ),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  publishTo := (if (isSnapshot.value) None else localStaging.value),
  homepage := Some(url("https://github.com/wartremover/wartremover-contrib")),
  pomExtra :=
    <scm>
      <url>git@github.com:wartremover/wartremover-contrib.git</url>
      <connection>scm:git:git@github.com:wartremover/wartremover-contrib.git</connection>
    </scm>
    <developers>
      <developer>
        <name>Chris Neveu</name>
        <url>http://chrisneveu.com</url>
      </developer>
    </developers>,
)

commonSettings
publishArtifact := false
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommand("sonaRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

lazy val coreSettings = Def.settings(
  commonSettings,
  name := "wartremover-contrib",
  Test / scalacOptions += {
    val hash = (Compile / sources).value.map { f =>
      sbt.internal.inc.HashUtil.farmHash(f.toPath)
    }.sum
    s"-Dplease-recompile-because-main-source-files-changed-${hash}"
  },
  libraryDependencies ++= Seq(
    "joda-time" % "joda-time" % "2.14.0" % Test,
    "org.scalatest" %% "scalatest-funsuite" % "3.2.19" % Test
  )
)

lazy val coreBinary = projectMatrix
  .in(file("core"))
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(
    scalaVersions = Seq(
      scala212Latest,
      scala213Latest,
      scala3Latest
    ),
  )
  .settings(
    coreSettings,
    crossVersion := CrossVersion.binary,
    libraryDependencies ++= {
      if (scalaBinaryVersion.value == "3") {
        Seq(
          "org.scala-lang" %% "scala3-tasty-inspector" % scalaVersion.value % Test,
          "io.get-coursier" % "coursier" % "2.1.24" % Test cross CrossVersion.for3Use2_13 exclude (
            "org.scala-lang.modules",
            "scala-xml_2.13"
          ),
          "org.scala-sbt" %% "io" % "1.10.5" % Test,
          "org.wartremover" %% "wartremover-inspector" % wartremoverVersion % Test,
        )
      } else {
        Nil
      }
    },
    libraryDependencies ++= Seq(
      "org.wartremover" %% "wartremover" % wartremoverVersion cross CrossVersion.binary
    ),
  )

lazy val coreFull = projectMatrix
  .in(file("core-full"))
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(
    scalaVersions = Seq(
      scala212Versions,
      scala213Versions,
      scala3Versions
    ).flatten,
    crossVersion = CrossVersion.full
  )
  .settings(
    coreSettings,
    Compile / scalaSource := Def.settingDyn {
      val p = coreBinary.jvm(
        scalaBinaryVersion.value match {
          case "3" =>
            scala3Latest
          case "2.13" =>
            scala213Latest
          case "2.12" =>
            scala212Latest
        }
      )
      Def.setting(
        (p / Compile / scalaSource).value
      )
    }.value,
    crossVersion := CrossVersion.full,
    crossTarget := {
      // workaround for https://github.com/sbt/sbt/issues/5097
      target.value / s"scala-${scalaVersion.value}"
    },
    libraryDependencies ++= Seq(
      "org.wartremover" %% "wartremover" % wartremoverVersion cross CrossVersion.full
    ),
  )

lazy val sbtPlug: ProjectMatrix = projectMatrix
  .in(file("sbt-plugin"))
  .withId("sbt-plugin")
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(
    scalaVersions = Seq(
      scala212Latest,
      "3.7.2",
    ),
  )
  .settings(
    commonSettings,
    sbtPlugin := true,
    scriptedBatchExecution := false,
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" =>
          sbtVersion.value
        case _ =>
          "2.0.0-RC6"
      }
    },
    name := "sbt-wartremover-contrib",
    scriptedBufferLog := false,
    scriptedLaunchOpts ++= {
      val javaVmArgs = {
        import scala.collection.JavaConverters._
        java.lang.management.ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList
      }
      javaVmArgs.filter(a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith))
    },
    scriptedLaunchOpts += ("-Dplugin.version=" + version.value),
    addSbtPlugin("org.wartremover" %% "sbt-wartremover" % wartremoverVersion),
    (Compile / sourceGenerators) += Def.task {
      val base = (Compile / sourceManaged).value
      val file = base / "wartremover" / "contrib" / "Wart.scala"
      val wartsDir = coreBinary.base / "src" / "main" / "scala-2" / "org" / "wartremover" / "contrib" / "warts"
      val deprecatedWarts = Set("NoNeedImport")
      val warts: Seq[String] = wartsDir.listFiles
        .withFilter(f => f.getName.endsWith(".scala") && f.isFile)
        .map(_.getName.replaceAll("""\.scala$""", ""))
        .filterNot(deprecatedWarts)
        .sorted
      val expectCount = 13
      assert(
        warts.size == expectCount,
        s"${warts.size} != ${expectCount}. please update build.sbt when add or remove wart"
      )
      val content =
        s"""package wartremover.contrib
           |import wartremover.Wart
           |// Autogenerated code, see build.sbt.
           |object ContribWart {
           |  @deprecated(message = "use ContribVersion", since = "2.0.0-RC1")
           |  def ContribVersion$$: String = ContribVersion
           |  def ContribVersion: String = "${version.value}"
           |  def allBut(ws: Wart*): List[Wart] = All.filterNot(w => ws.exists(_.clazz == w.clazz))
           |  /** A fully-qualified class name of a custom Wart implementing `org.wartremover.WartTraverser`. */
           |  private[this] def w(nm: String): Wart = new Wart(s"org.wartremover.contrib.warts.$$nm")
           |  @deprecated("move to core https://github.com/wartremover/wartremover/commit/25b3a07a912c5f82f", "2.0.0") def NoNeedImport: Wart = _root_.wartremover.Wart.NoNeedImport
           |""".stripMargin +
          warts.map(w => s"""  val $w: Wart = w("${w}")""").mkString("", "\n", "\n") +
          s"""  val All: List[Wart] = List(${warts mkString ", "})""" +
          "\n}\n"
      IO.write(file, content)
      Seq(file)
    }
  )
  .enablePlugins(ScriptedPlugin)

ThisBuild / sbtPluginPublishLegacyMavenStyle := false
