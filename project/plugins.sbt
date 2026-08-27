addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.5.0")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.5.0")

addSbtPlugin("pl.project13.scala" % "sbt-jmh" % "0.4.8")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.2")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

libraryDependencies += "com.github.xuwei-k" %% "scala-version-from-sbt-version" % "0.1.0"
