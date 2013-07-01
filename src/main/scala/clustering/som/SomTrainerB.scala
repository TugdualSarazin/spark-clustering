package clustering.som

import spark.{SparkContext, RDD}
import spark.util.Vector
import SparkContext._
import scala.collection.Map


/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 14/06/13
 * Time: 14:29
 */
class SomTrainerB extends SomTrainerA {
  override protected def trainingIteration(dataset: RDD[Vector], currentIt: Int, maxIt: Int): Double = {
    // Find the closest model and create observations
    val closest = dataset.flatMap{d =>
      val closestNeuron = _somModel.findClosestPrototype(d).asInstanceOf[SomNeuron]
      Array.tabulate(_somModel.size){i =>
        val neuron = _somModel(i).asInstanceOf[SomNeuron]
        val factorDist = neuron.factorDist(closestNeuron, super.processT(maxIt, currentIt))
        (neuron.id, new SomObsB(d, factorDist))
      }
    }

    // Merge observations
    val sumSomObs = closest.reduceByKey(_ + _).collectAsMap()

    // Update model and process convergence distance
    updateModel(sumSomObs)
  }

  class SomObsB(var point: Vector, var factorDist: Double) extends Serializable {
    point *= factorDist

    def +(obs: SomObsB): SomObsB = {
      point += obs.point
      factorDist += obs.factorDist
      this
    }

    override def toString: String = "point:"+point+" - factorDist:"+factorDist
  }

  private def updateModel(mapObs: Map[Int, SomObsB]): Double = {
    var dist = 0.0

    mapObs.foreach { case (key, obs) =>
      //println("Key:"+key+" = ["+obs+"]")
      //println(this(key).point+" - "+obs.point+" = "+this(key).point.squaredDist(obs.point))
      val newPoint = obs.point / obs.factorDist
      dist += _somModel(key)._point.squaredDist(newPoint)
      _somModel(key)._point = newPoint
    }
    dist
  }
}
