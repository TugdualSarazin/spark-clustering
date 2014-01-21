import AssemblyKeys._

assemblySettings

organization := "org.altic.spark.clustering"

name := "spark-clustering"

version := "0.8.1-SNAPSHOT"

scalaVersion := "2.9.3"

libraryDependencies ++= Seq(
  ("org.apache.spark" %% "spark-core" % "0.8.1-incubating").
    exclude("org.mortbay.jetty", "servlet-api").
    exclude("commons-beanutils", "commons-beanutils-core").
    exclude("commons-collections", "commons-collections").
    exclude("commons-collections", "commons-collections").
    exclude("com.esotericsoftware.minlog", "minlog")
)

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("about.html") => MergeStrategy.rename
    case x => old(x)
  }
}

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

