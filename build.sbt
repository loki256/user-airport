name := "user-airport"

organization := "loki256.github.com"

version := "0.0.1"

scalaVersion := "2.12.2"

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.3" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.13.5" % "test" withSources() withJavadoc(),
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.rogach" %% "scallop" % "2.1.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",
  "ch.hsr" % "geohash" % "1.3.0"
)

initialCommands := "import loki256.github.com.userairport._"
