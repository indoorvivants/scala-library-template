lazy val root = project
  .aggregate(
    (core.projectRefs): _*
  )
  .settings(
    skip in publish := true
  )

Global / excludeLintKeys += logManager
Global / excludeLintKeys += scalaJSUseMainModuleInitializer
Global / excludeLintKeys += scalaJSLinkerConfig

inThisBuild(
  List(
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0",
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

lazy val nonJVMDependencySettings = Seq(
// https://github.com/cb372/sbt-explicit-dependencies/issues/27
  unusedCompileDependenciesTest := {},
  missinglinkCheck := {},
  undeclaredCompileDependenciesTest := {}
)

val scala2Versions = Seq("2.13.4", "2.12.13")
val scala3Versions = Seq("3.0.0-M3")

lazy val munitSettings = Seq(
  libraryDependencies += {
    "org.scalameta" %%% "munit" % "0.7.21" % Test
  },
  testFrameworks += new TestFramework("munit.Framework")
)

lazy val core = projectMatrix
  .in(file("modules/core"))
  .settings(
    name := "core",
    scalacOptions.in(Test) ~= filterConsoleScalacOptions
  )
  .settings(munitSettings)
  .jvmPlatform(scala2Versions ++ scala3Versions)
  .jsPlatform(scala2Versions ++ scala3Versions, nonJVMDependencySettings)
  .nativePlatform(scala2Versions, nonJVMDependencySettings)
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

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "ProcedureSyntax",
  "NoValInForComprehension"
).mkString(" ")

val CICommands = Seq(
  "clean",
  "compile",
  "test",
  "core/scalafmtCheckAll",
  s"core/scalafix --check $scalafixRules",
  "core/headerCheck",
  "undeclaredCompileDependenciesTest",
  "unusedCompileDependenciesTest",
  "core/missinglinkCheck"
).mkString(";")

val PrepareCICommands = Seq(
  s"core/compile:scalafix --rules $scalafixRules",
  s"core/test:scalafix --rules $scalafixRules",
  "core/test:scalafmtAll",
  "core/compile:scalafmtAll",
  "core/scalafmtSbt",
  "core/headerCreate",
  "undeclaredCompileDependenciesTest"
).mkString(";")

addCommandAlias("ci", CICommands)

addCommandAlias("preCI", PrepareCICommands)
