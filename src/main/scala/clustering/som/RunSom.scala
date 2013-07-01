package clustering.som

import spark.util.Vector
import spark.{RDD, SparkContext}
import clustering.utils.DataGenerator
import clustering.{AbstractTrainer, AbstractModel}

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 14/06/13
 * Time: 12:34
 */

object RunSom extends App {
  val somOptions = Map("clustering.som.nbrow" -> "5", "clustering.som.nbcol" -> "5")
  val trainer = new SomTrainerB
  val convergeDist = -0.001
  val maxIter = 30
  val dims = 10
  val N = 200
  val outputPath = "/home/tug/workspaceWeb/somVizu/somDataset.js"


  val sc = new SparkContext("local", "RunSom")

  // Generate data from random
  val trainingDataset = sc.parallelize(DataGenerator.vector2ClsND(N, dims), 2).cache()

  trainingAndPrint(trainer, trainingDataset, somOptions, maxIter, convergeDist)

  def parseVector(line: String): Vector = new Vector(line.split(' ').map(_.toDouble))

  def trainingAndPrint(trainer: AbstractTrainer,
                       dataset: RDD[Vector],
                       modelOptions: Map[String, String],
                       maxIteration: Int = 100,
                       endConvergeDistance: Double): AbstractModel = {
    val model = trainer.training(trainingDataset, somOptions, maxIter, convergeDist)

    println("-- Convergence Distance : "+trainer.getLastConvergence)
    println("-- NbIteration : "+trainer.getLastIt)
    println("-- Training duration : "+trainer.getLastTrainingDuration)
    println("-- FinalModel :\n"+model)

    model
  }
}
