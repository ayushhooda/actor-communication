name := """nice"""
organization := "nice"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.10" % "no-provider"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"

libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.12"
