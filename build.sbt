Global / excludeLintKeys += logManager
Global / excludeLintKeys += scalaJSUseMainModuleInitializer
Global / excludeLintKeys += scalaJSLinkerConfig

inThisBuild(
  List(
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := scalaBinaryVersion.value,
    organization := "com.indoorvivants",
    organizationName := "Anton Sviridov",
    homepage := Some(
      url("https://github.com/indoorvivants/scala-library-template")
    ),
    startYear := Some(2020),
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "keynmol",
        "Anton Sviridov",
        "keynmol@gmail.com",
        url("https://blog.indoorvivants.com")
      )
    )
  )
)

// https://github.com/cb372/sbt-explicit-dependencies/issues/27
lazy val disableDependencyChecks = Seq(
  unusedCompileDependenciesTest := {},
  missinglinkCheck := {},
  undeclaredCompileDependenciesTest := {}
)

val Scala213 = "2.13.10"
val Scala212 = "2.12.17"
val Scala3 = "3.2.2"
val scalaVersions = Seq(Scala3, Scala212, Scala213)

lazy val munitSettings = Seq(
  libraryDependencies += {
    "org.scalameta" %%% "munit" % "1.0.0-M7" % Test
  },
  testFrameworks += new TestFramework("munit.Framework")
)

lazy val root = projectMatrix
  .aggregate(core)

lazy val core = projectMatrix
  .in(file("modules/core"))
  .settings(
    name := "core",
    Test / scalacOptions ~= filterConsoleScalacOptions
  )
  .settings(munitSettings)
  .jvmPlatform(scalaVersions)
  .jsPlatform(scalaVersions, disableDependencyChecks)
  .nativePlatform(scalaVersions, disableDependencyChecks)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoPackage := "com.indoorvivants.library.internal",
    buildInfoKeys := Seq[BuildInfoKey](
      version,
      scalaVersion,
      scalaBinaryVersion
    ),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )

lazy val docs = project
  .in(file("myproject-docs"))
  .settings(
    scalaVersion := Scala213,
    mdocVariables := Map(
      "VERSION" -> version.value
    ),
    publish / skip := true,
    publishLocal / skip := true
  )
  .settings(disableDependencyChecks)
  .dependsOn(core.jvm(Scala213))
  .enablePlugins(MdocPlugin)

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "NoValInForComprehension"
).mkString(" ")

val CICommands = Seq(
  "clean",
  "compile",
  "test",
  "docs/mdoc",
  "scalafmtCheckAll",
  "scalafmtSbtCheck",
  s"scalafix --check $scalafixRules",
  "headerCheck",
  "undeclaredCompileDependenciesTest",
  "unusedCompileDependenciesTest",
  "missinglinkCheck"
).mkString(";")

val PrepareCICommands = Seq(
  s"scalafix --rules $scalafixRules",
  "scalafmtAll",
  "scalafmtSbt",
  "headerCreate",
  "undeclaredCompileDependenciesTest"
).mkString(";")

addCommandAlias("ci", CICommands)

addCommandAlias("preCI", PrepareCICommands)
