val scala3Version = "3.3.1"
val tapirVersion = "1.9.1"
val refinedVersion = "0.11.0"
val circeVersion = "0.14.5"

enablePlugins(
  JavaAppPackaging,
  DockerPlugin
)

Docker / packageName := "dcolle/todoback-server"
dockerBaseImage := "openjdk:21-jdk"
dockerExposedPorts ++= Seq(8080)

lazy val root = project
  .in(file("."))
  .settings(
    name := "todoback",
    version := "0.1.0-SNAPSHOT",
    organization := "com.test.todoback",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "eu.timepit" %% "refined"  % refinedVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-cats" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-refined" % tapirVersion,
      "ch.qos.logback" % "logback-classic" % "1.4.11",
      "org.http4s" %% "http4s-ember-server" % "0.23.24",
      "ch.qos.logback" % "logback-classic" % "1.4.11",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-refined" % circeVersion,
      "org.latestbit" %% "circe-tagged-adt-codec" % "0.11.0",
      "com.typesafe" % "config" % "1.4.3",
      "org.reactivemongo" %% "reactivemongo" % "1.1.0-RC11"
    )
  )