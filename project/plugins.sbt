addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.21")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.5")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.3.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
