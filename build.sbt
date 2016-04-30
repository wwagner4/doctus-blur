lazy val _scalaVersion = "2.11.8"
lazy val doctusVersion = "1.0.6-SNAPSHOT"
lazy val mockitoVersion = "1.9.5"
lazy val utestVersion = "0.4.1"

lazy val commonSettings = 
  Seq(
    version := "0.1.0-SNAPSHOT",
    scalaVersion := _scalaVersion,
    organization := "net.entelijan",
    organizationHomepage := Some(url("http://entelijan.net/")),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    EclipseKeys.withSource := true)

lazy val coreSettings =
  commonSettings ++
    Seq(
      libraryDependencies += "net.entelijan" %%% "doctus-core" % doctusVersion,
      libraryDependencies += "com.lihaoyi" %%% "utest" % utestVersion % "test",
      testFrameworks += new TestFramework("utest.runner.Framework"))

lazy val jvmSettings =
  commonSettings ++
    Seq(
      libraryDependencies += "net.entelijan" %% "doctus-jvm" % doctusVersion,
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      libraryDependencies += "com.lihaoyi" %% "upickle" % "0.4.0",
      fork := true,
      testFrameworks += new TestFramework("utest.runner.Framework"))

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "blur-root")
  .aggregate(
    core,
    jvm)

lazy val core = (project in file("blur-core"))
  .settings(coreSettings: _*)
  .settings(
    name := "blur-core")
  .enablePlugins(ScalaJSPlugin)

lazy val jvm = (project in file("blur-jvm"))
  .settings(jvmSettings: _*)
  .settings(
    name := "blur-jvm")
  .dependsOn(core)


