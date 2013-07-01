
organization := "org.altic"

name := "spark-clustering"

//version := "0.7"
version := "0.7.3-SNAPSHOT"
//version := "0.8-SNAPSHOT"
//version := "0.6.2"

//scalaVersion := "2.9.2"
scalaVersion := "2.9.3"
//scalaVersion := "2.10.0"

//libraryDependencies += "org.spark-project" %% "spark-core" % "0.6.2"
//libraryDependencies += "org.spark-project" %% "spark-core" % "0.7.0"
libraryDependencies += "org.spark-project" %% "spark-core" % "0.7.3-SNAPSHOT"
//libraryDependencies += "org.spark-project" %% "spark-core" % "0.8.0-SNAPSHOT"

resolvers ++= Seq(
		"Akka Repository" at "http://repo.akka.io/releases/",
		"Spray Repository" at "http://repo.spray.cc/")

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

