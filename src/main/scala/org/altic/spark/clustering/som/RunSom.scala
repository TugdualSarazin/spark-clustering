package org.altic.spark.clustering.som

import org.apache.spark.util.Vector
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.altic.spark.clustering.utils.{NamedVector, DataGenerator}
import org.altic.spark.clustering.global.{AbstractModel, AbstractTrainer}

//object RunSom extends App {
object RunSom {
  val somOptions = Map("clustering.som.nbrow" -> "5", "clustering.som.nbcol" -> "5")
  val trainer = new SomTrainerB
  val convergeDist = -0.001
  val maxIter = 30
  val dataNbObs = 20000
  val dataNbVars = 20
  val outputPath = "/home/tug/workspaceWeb/somVizu/somDataset.js"

  def parseVector(line: String): Vector = new Vector(line.split(' ').map(_.toDouble))

  val sc = new SparkContext("local", "RunSom")

  /*System.setProperty("spark.local.dir", "/home/tmpSpark")
  System.setProperty("SPARK_MEM", "8g")
  val sc = new SparkContext(
    "spark://srv0:7077",
    "SomSpartakus",
    "/opt/spark-0.7.3",
    Seq("/opt/ScalaProjects/Spartakus/target/scala-2.9.3/spartakus-core_2.9.3-0.7.3-SNAPSHOT.jar")
  )*/

  // Generate data from random
  //val trainingDataset = sc.parallelize(DataGenerator.vector2Cls2D(N), 2).cache()
  val trainingDataset = sc.parallelize(DataGenerator.gen2ClsNDims(dataNbObs, dataNbVars).getVector, 2)
  //trainingDataset.saveAsObjectFile("hdfs:///tmp/training.object")

  //val trainingDataset = sc.objectFile("hdfs:///tmp/training.object", 5).asInstanceOf[RDD[Vector]]

  trainingAndPrint(trainer, trainingDataset, somOptions, maxIter, convergeDist)

  // Write clusters on js
  val evalDataset = sc.parallelize(DataGenerator.gen2ClsNDims(dataNbObs, dataNbVars).getNamedVector, 2).cache()
  //println("Purity = "+purity(model, evalDataset))
  //WriterClusters.js(evalDataset, somModel2, outputPath)
  //WriterClusters.js(evalDataset, model, outputPath)

  def purity(model: AbstractModel, dataset: RDD[NamedVector]): Double = {
    //val nbRealClass = dataset.map(_.cls).reduce(case(cls1,cls2))

    val sumAffectedDatas = dataset.map(d => ((model.findClosestPrototype(d).id, d.cls), 1))
      .reduceByKey{case (sum1, sum2) => sum1+sum2}

    val maxByCluster = sumAffectedDatas.map(sa => (sa._1._1, sa._2))
      .reduceByKey{case (sum1, sum2) => sum1.max(sum2) }
      .map(_._2)
      .collect()

    maxByCluster.sum / dataset.count().toDouble
  }

  def trainingAndPrint(trainer: AbstractTrainer,
                       dataset: RDD[Vector],
                       modelOptions: Map[String, String],
                       maxIteration: Int = 100,
                       endConvergeDistance: Double): AbstractModel = {
    val model = trainer.training(trainingDataset, somOptions, maxIter, convergeDist)
    // Initi  alisation du model
    //val trainer = new SomTrainer(nbRow, nbCol, trainingDataset, convergeDist, maxIter)
    //val model = trainer.model

    println("-- Convergence Distance : "+trainer.getLastConvergence)
    println("-- NbIteration : "+trainer.getLastIt)
    println("-- Training duration : "+trainer.getLastTrainingDuration)
    println("-- FinalModel :\n"+model)

    model
  }
}
