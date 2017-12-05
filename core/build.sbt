lazy val akkaVersion = "2.5.3"

name := "resumable-downloader"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.github.tomakehurst" % "wiremock" % "1.33" % "test",
  "com.typesafe.akka" %% "akka-stream" % "2.5.6",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.6" % Test
)
