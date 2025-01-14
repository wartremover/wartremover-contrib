addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.21")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.0")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.3.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.3")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
