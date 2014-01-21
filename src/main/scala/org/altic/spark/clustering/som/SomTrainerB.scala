package org.altic.spark.clustering.som

import scala.collection.Map
import scala.collection.mutable.Stack
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext._
import org.apache.spark.util.Vector

/**
 * User: tug
 * Date: 14/06/13
 * Time: 14:29
 */
class SomTrainerB extends SomTrainerA {

  val quantErrors = new Stack[Double]

  override protected def trainingIteration(dataset: RDD[Vector], currentIt: Int, maxIt: Int): Double = {
    // process quantification error
    //quantErrors.push(quantError(dataset))

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

    override def toString = "point:"+point+" - factorDist:"+factorDist
  }

  private def quantError(dataset: RDD[Vector]): Double = {
    dataset.map{d => _somModel.findClosestPrototype(d).dist(d)}.reduce(_+_)
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
