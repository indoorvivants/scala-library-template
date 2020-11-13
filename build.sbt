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
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.3",
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

val scala2Version  = Seq("2.13.3", "2.12.12")
val dottyVersion   = Seq("0.27.0-RC1")
val scala3Version  = Seq("3.0.0-M1")
val scala11Version = Seq("2.11.12")

lazy val munitSettings = Seq(
  libraryDependencies += {
    "org.scalameta" %%% "munit" % "0.7.17" % Test
  },
  testFrameworks += new TestFramework("munit.Framework")
)

lazy val core = projectMatrix
  .in(file("modules/core"))
  .settings(
    name := "core",
    scalacOptions.in(Test) ~= filterConsoleScalacOptions
  )
  .jvmPlatform(
    scalaVersions = scala2Version ++ scala3Version,
    settings = munitSettings
  )
  .jsPlatform(
    scalaVersions = scala2Version ++ scala3Version,
    settings = munitSettings
  )
  .jsPlatform(
    scalaVersions = dottyVersion,
    settings = Seq(
      test := {}
    )
  )
  .nativePlatform(
    scalaVersions = scala11Version,
    settings = munitSettings ++ Seq(
      publishArtifact in (Compile, packageDoc) := false
    )
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoPackage := "com.indoorvivants.library.internal",
    buildInfoKeys := Seq[BuildInfoKey](
      version,
      scalaVersion,
      scalaBinaryVersion
    ),
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)),
    publishArtifact in (Compile, packageDoc) :=
      !scalaVersion.value.startsWith("0.27")
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
