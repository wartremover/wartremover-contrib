addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.13")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.0.0")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.6")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
