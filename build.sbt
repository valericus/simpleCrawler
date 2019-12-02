val akkaVersion = "2.6.0"
val akkaHttpVersion = "10.1.11"
val circeVersion = "0.12.1"

name := "simpleCrawler"

version := "0.2"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "org.jsoup" % "jsoup" % "1.12.1",

  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

  "de.heikoseeberger" %% "akka-http-circe" % "1.29.1",

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,

  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "org.scalamock" %% "scalamock" % "4.4.0" % Test
)