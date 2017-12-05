import sbt.Keys.version

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.12.2"
)

lazy val core = (project in file("core")).settings(commonSettings)

lazy val examples = (project in file("examples")).settings(commonSettings).dependsOn(core)

lazy val root = (project in file(".")).settings(commonSettings).aggregate(core)

