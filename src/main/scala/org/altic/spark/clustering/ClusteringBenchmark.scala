package org.altic.spark.clustering

import org.apache.spark.SparkContext
import org.altic.spark.clustering.global.AbstractTrainer
import org.altic.spark.clustering.som.{SomTrainerB, SomTrainerA}
import org.altic.spark.clustering.utils.DataGenerator

/**
 * User: tug
 * Date: 17/06/13
 * Time: 11:05
 */
//object ClusteringBenchmark extends App {
object ClusteringBenchmark {
  val somOptions = Map("clustering.som.nbrow" -> "5", "clustering.som.nbcol" -> "5")
  val trainers = Array[AbstractTrainer](new SomTrainerA, new SomTrainerB)
  val convergeDist = -0.001
  val maxIter = 30
  val dims = 10
  val Ns = Iterator.iterate(1E2)(_ * 10).takeWhile(_ <= 1E4)

  val sc = new SparkContext("local", "RunSom")
  /*System.setProperty("spark.local.dir", "/home/tmpSpark")
  System.setProperty("SPARK_MEM", "8g")
  val sc = new SparkContext(
    "spark://srv0:7077",
    "SomSpartakus",
    "/opt/spark-0.7.3",
    Seq("/opt/ScalaProjects/Spartakus/target/scala-2.9.3/spartakus-core_2.9.3-0.7.3-SNAPSHOT.jar")
  )*/

  Ns.foreach{N =>
    val minSplit = (N / 1E3).max(1).toInt

    val trainingDataset = sc.parallelize(DataGenerator.gen2ClsNDims(N.toInt, dims).getVector, minSplit).cache()

    trainers.foreach{t =>
      t.training(trainingDataset, somOptions, maxIter, convergeDist)
      println("### "+t.getClass.getSimpleName+" , "+N+" : "+t.getLastTrainingDuration)
    }
  }
}
