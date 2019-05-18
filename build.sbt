import ReleaseTransformations._
import com.typesafe.sbt.pgp.PgpKeys._
import com.typesafe.sbt.pgp.PgpSettings.useGpg

val wartremoverVersion = "2.4.2"
val scala211 = "2.11.12"
val scala212 = "2.12.8"
val scala213 = "2.13.0-RC1"

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
  publishArtifact in Test := false,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  homepage := Some(url("https://www.wartremover.org")),
  useGpg := false,
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
  scalaVersion := scala212,
)

lazy val root = Project(
  id = "wartremover-contrib",
  base = file("."),
).settings(
  commonSettings,
  publishArtifact := false,
  releaseCrossBuild := true,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("+publishSigned"),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
).aggregate(
  core,
  sbtPlug,
)

lazy val core = Project(
  id = "core",
  base = file("core")
).settings(
  commonSettings,
  name := "wartremover-contrib",
  crossScalaVersions := Seq(scala211, scala212, scala213),
  libraryDependencies ++= Seq(
    "org.wartremover" %% "wartremover" % wartremoverVersion
  ),
  libraryDependencies ++= {
    Seq(
      "org.scalatest" %% "scalatest" % "3.0.8-RC3" % Test
    )
  }
)

/**
  * Workaround for https://github.com/sbt/sbt/issues/3393.
  */
def addSbtPluginHack(dependency: ModuleID): Setting[Seq[ModuleID]] =
  libraryDependencies += {
    val sbtV = (sbtBinaryVersion in pluginCrossBuild).value
    val scalaV = (scalaBinaryVersion in update).value
    Defaults.sbtPluginExtra(dependency, sbtV, scalaV)
  }

lazy val sbtPlug: Project = Project(
  id = "sbt-plugin",
  base = file("sbt-plugin")
).settings(
  commonSettings,
  sbtPlugin := true,
  name := "sbt-wartremover-contrib",
  scriptedBufferLog := false,
  scriptedLaunchOpts ++= {
    val javaVmArgs = {
      import scala.collection.JavaConverters._
      java.lang.management.ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList
    }
    javaVmArgs.filter(
      a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
    )
  },
  scriptedLaunchOpts += ("-Dplugin.version=" + version.value),
  crossScalaVersions := Seq(scala212),
  addSbtPluginHack("org.wartremover" %% "sbt-wartremover" % wartremoverVersion),
  sourceGenerators in Compile += Def.task {
    val base = (sourceManaged in Compile).value
    val file = base / "wartremover" / "contrib" / "Wart.scala"
    val wartsDir = core.base / "src" / "main" / "scala" / "wartremover" / "contrib" / "warts"
    val warts: Seq[String] = wartsDir
      .listFiles
      .withFilter(f => f.getName.endsWith(".scala") && f.isFile)
      .map(_.getName.replaceAll("""\.scala$""", ""))
      .sorted
    val content =
      s"""package wartremover.contrib
         |import wartremover.Wart
         |// Autogenerated code, see build.sbt.
         |object ContribWart {
         |  val ContribVersion$$ = "${version.value}"
         |  lazy val All: collection.Seq[Wart] = List(${warts mkString ", "})
         |  private[wartremover] lazy val ContribWarts = List(${warts mkString ", "})
         |  /** A fully-qualified class name of a custom Wart implementing `org.wartremover.WartTraverser`. */
         |  private[this] def w(nm: String): Wart = new Wart(s"org.wartremover.contrib.warts.$$nm")
         |""".stripMargin +
        warts.map(w => s"""  val $w = w("${w}")""").mkString("\n") + "\n}\n"
    IO.write(file, content)
    Seq(file)
  }
).enablePlugins(ScriptedPlugin)
