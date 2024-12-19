addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.9.2")

addSbtPlugin("com.eed3si9n" % "sbt-projectmatrix" % "0.10.1")

// Code quality
addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.2")

addSbtPlugin("ch.epfl.scala" % "sbt-missinglink" % "0.3.6")

addSbtPlugin("com.github.cb372" % "sbt-explicit-dependencies" % "0.3.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.13.0")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

addSbtPlugin("de.heikoseeberger" % "sbt-header" % "5.10.0")

// Compiled documentation
addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.6.2")

// Scala.js and Scala Native
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.17.0")

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.6")
