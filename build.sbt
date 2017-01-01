import ReleaseTransformations._
import com.typesafe.sbt.pgp.PgpKeys._
import com.typesafe.sbt.pgp.PgpSettings.useGpg

lazy val commonSettings = Seq(
  organization := "org.wartremover",
  licenses := Seq(
    "The Apache Software License, Version 2.0" ->
      url("http://www.apache.org/licenses/LICENSE-2.0.txt")
  ),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo <<= version { (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  homepage := Some(url("http://wartremover.org")),
  useGpg := true,
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
    </developers>
)

lazy val root = Project(
  id = "wartremover-contrib",
  base = file(".")
).settings(commonSettings ++ Seq(
  name := "wartremover-contrib",
  scalaVersion := "2.11.8",
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  libraryDependencies := {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) =>
        libraryDependencies.value :+ ("org.scalamacros" %% "quasiquotes" % "2.0.1")
      case _ =>
        libraryDependencies.value
    }
  },
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  ),
  // a hack (?) to make `compile` and `+compile` tasks etc. behave sanely
  aggregate := CrossVersion.partialVersion((scalaVersion in Global).value) == Some((2, 10)),
  publishArtifact := false,
  crossScalaVersions := Seq("2.11.8", "2.10.6", "2.12.0"),
  crossVersion := CrossVersion.binary,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts.copy(action = st => { // publish *everything* with `sbt "release cross"`
    val extracted = Project.extract(st)
      val ref = extracted.get(thisProjectRef)
      extracted.runAggregated(publishSigned in Global in ref, st)
    }),
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
): _*)
