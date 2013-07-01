package clustering

import spark.{RDD, SparkContext}
import clustering.som.{SomTrainerA, SomTrainerB}
import scala.math.pow
import spark.util.Vector
import clustering.utils.DataGenerator
import Stream._

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 17/06/13
 * Time: 11:05
 */
object ClusteringBenchmark extends App {
  val somOptions = Map("clustering.som.nbrow" -> "5", "clustering.som.nbcol" -> "5")
  val trainers = Array[AbstractTrainer](new SomTrainerA, new SomTrainerB)
  val convergeDist = -0.001
  val maxIter = 30
  val dims = 10
  val Ns = Iterator.iterate(1E2)(_ * 10).takeWhile(_ <= 1E4)

  val sc = new SparkContext("local", "ClusteringBenchmark")

  Ns.foreach{N =>
    val minSplit = (N / 1E3).max(1).toInt

    val trainingDataset = sc.parallelize(DataGenerator.vector2ClsND(N.toInt, dims), minSplit).cache()

    trainers.foreach{t =>
      t.training(trainingDataset, somOptions, maxIter, convergeDist)
      println("### "+t.getClass.getSimpleName+" , "+N+" : "+t.getLastTrainingDuration)
    }
  }
}
