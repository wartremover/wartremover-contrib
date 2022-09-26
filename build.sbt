import ReleaseTransformations._

Global / onChangedBuildSource := ReloadOnSourceChanges

val wartremoverVersion = "2.4.20"

val scala211Versions = Seq("2.11.12")
val scala212Versions = (10 to 17).map(n => s"2.12.${n}")
val scala213Versions = (0 to 9).map(n => s"2.13.${n}")

def latest(versions: Seq[String]) = {
  val prefix = versions.head.split('.').init.mkString("", ".", ".")
  assert(versions.forall(_ startsWith prefix))
  prefix + versions.map(_.drop(prefix.length).toLong).max
}

val scala211Latest = latest(scala211Versions)
val scala212Latest = latest(scala212Versions)
val scala213Latest = latest(scala213Versions)

lazy val commonSettings = Seq(
  organization := "org.wartremover",
  licenses := Seq(
    "The Apache Software License, Version 2.0" ->
      url("http://www.apache.org/licenses/LICENSE-2.0.txt")
  ),
  scalacOptions ++= Seq(
    "-deprecation"
  ),
  publishMavenStyle := true,
  Test / publishArtifact := false,
  publishTo := sonatypePublishToBundle.value,
  homepage := Some(url("https://www.wartremover.org")),
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
  scalaVersion := scala212Latest,
)

commonSettings
publishArtifact := false
releaseCrossBuild := true
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
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
    "joda-time" % "joda-time" % "2.11.2" % Test,
    "org.scalatest" %% "scalatest" % "3.2.13" % Test
  )
)

lazy val coreBinary = project
  .in(file("core"))
  .settings(
    coreSettings,
    crossScalaVersions := Seq(scala211Latest, scala212Latest, scala213Latest),
    crossVersion := CrossVersion.binary,
    libraryDependencies ++= Seq(
      "org.wartremover" %% "wartremover" % wartremoverVersion cross CrossVersion.binary
    ),
  )

lazy val coreFull = project
  .in(file("core-full"))
  .settings(
    coreSettings,
    crossScalaVersions := Seq(scala211Versions, scala212Versions, scala213Versions).flatten,
    Compile / scalaSource := (coreBinary / Compile / scalaSource).value,
    crossVersion := CrossVersion.full,
    crossTarget := {
      // workaround for https://github.com/sbt/sbt/issues/5097
      target.value / s"scala-${scalaVersion.value}"
    },
    libraryDependencies ++= Seq(
      "org.wartremover" %% "wartremover" % wartremoverVersion cross CrossVersion.full
    ),
  )

lazy val sbtPlug: Project = Project(
  id = "sbt-plugin",
  base = file("sbt-plugin")
).settings(
  commonSettings,
  sbtPlugin := true,
  scriptedBatchExecution := false,
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
  crossScalaVersions := Seq(scala212Latest),
  addSbtPlugin("org.wartremover" %% "sbt-wartremover" % wartremoverVersion),
  (Compile / sourceGenerators) += Def.task {
    val base = (Compile / sourceManaged).value
    val file = base / "wartremover" / "contrib" / "Wart.scala"
    val wartsDir = coreBinary.base / "src" / "main" / "scala-2" / "org" / "wartremover" / "contrib" / "warts"
    val warts: Seq[String] = wartsDir.listFiles
      .withFilter(f => f.getName.endsWith(".scala") && f.isFile)
      .map(_.getName.replaceAll("""\.scala$""", ""))
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
         |""".stripMargin +
        warts.map(w => s"""  val $w: Wart = w("${w}")""").mkString("", "\n", "\n") +
        s"""  val All: List[Wart] = List(${warts mkString ", "})""" +
        "\n}\n"
    IO.write(file, content)
    Seq(file)
  }
).enablePlugins(ScriptedPlugin)
