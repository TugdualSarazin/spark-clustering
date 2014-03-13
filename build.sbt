import AssemblyKeys._

assemblySettings

organization := "org.altic.spark.clustering"

name := "spark-clustering"

version := "0.9.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies += "org.apache.spark" %% "spark-core" % "0.9.0-incubating"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

