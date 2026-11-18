addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.5.0")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.6.0")

addSbtPlugin("com.github.sbt" % "sbt-jmh" % "0.4.9")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.4.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
