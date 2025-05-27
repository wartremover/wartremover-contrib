addSbtPlugin("com.eed3si9n" % "sbt-projectmatrix" % "0.11.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.1")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.4")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
