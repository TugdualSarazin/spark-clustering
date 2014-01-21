package org.altic.spark.clustering

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

object SimpleApp {
  def main(args: Array[String]) {
		val logFile = "/home/tug/ScalaProjects/spark-clustering/build.sbt" // Should be some file on your system
			//val sc = new SparkContext("local", "Simple App", "YOUR_SPARK_HOME",
			//		List("target/scala-2.9.3/simple-project_2.9.3-1.0.jar"))
      val sc = new SparkContext("local", "Simple App")
			val logData = sc.textFile(logFile, 2).cache()
			val numAs = logData.filter(line => line.contains("a")).count()
			val numBs = logData.filter(line => line.contains("b")).count()
			println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
  }
}