package clustering.som

import spark.RDD
import spark.util.Vector
import clustering.{AbstractPrototype, AbstractModel, AbstractTrainer}
import scala.math.{abs, exp}

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 14/06/13
 * Time: 12:35
 */
class SomTrainerA extends AbstractTrainer {
  val DEFAULT_SOM_ROW = 10
  val DEFAULT_SOM_COL = 10

  protected var _somModel: SomModel = null
  protected def getModel: AbstractModel = _somModel

  protected def initModel(dataset: RDD[Vector], modelOptions: Map[String, String]) {
    var nbRow = DEFAULT_SOM_ROW
    if (modelOptions != null) {
      nbRow = modelOptions("clustering.som.nbrow").toInt
    }

    var nbCol = DEFAULT_SOM_COL
    if (modelOptions != null) {
      nbCol = modelOptions("clustering.som.nbcol").toInt
    }

    val mapSize = nbRow * nbCol
    // todo : replace random = 42
    val selectedDatas = dataset.takeSample(false, mapSize, 42)

    // todo : Check /nbCol et %nbCOl
    val neuronMatrix = Array.tabulate(mapSize)(id => new SomNeuron(id, id/nbCol, id%nbCol, selectedDatas(id)))
    _somModel = new SomModel(nbRow, nbCol, neuronMatrix)
  }

  protected def trainingIteration(dataset: RDD[Vector], currentIteration: Int, maxIteration: Int): Double = {
    val T = processT(maxIteration, currentIteration)

    // create som observations
    val mapping = dataset.map{d =>
      val bestNeuron = _somModel.findClosestPrototype(d).asInstanceOf[SomNeuron]

      _somModel.prototypes.map{proto =>
        val neuron = proto.asInstanceOf[SomNeuron]
        val factor = neuron.factorDist(bestNeuron, T)
        new SomObsA(d * factor, factor, neuron.id)
      }
    }

    // Concat observations
    val concatObs = mapping.reduce{(obs1, obs2) =>
      for (i <- 0 until obs1.length) {
        obs1(i) += obs2(i)
      }
      obs1
    }

    // Update model and process convergence distance
    concatObs.map(_somModel.update(_)).sum
  }

  protected def processT(maxIt:Int, currentIt:Int) = maxIt.toFloat - currentIt

  protected class SomModel(val nbRow: Int, val nbCol: Int, neurons: Array[SomNeuron])
    extends AbstractModel(neurons.asInstanceOf[Array[AbstractPrototype]]) {

    // Update the data point of the neuron
    // and return the distance between the new and the old point
    def update(obs: SomObsA) = neurons(obs.neuronId).update(obs.compute)


    override def toString: String = {
      var str = ""
      for(neuron <- neurons) {
        str += neuron+"\n"
      }
      str
    }
  }

  protected class SomNeuron(id: Int, val row: Int, val col: Int, point: Vector) extends AbstractPrototype(id, point) {
    def factorDist(neuron: SomNeuron, T: Double): Double = {
      exp(-(abs(neuron.row - row) + abs(neuron.col - col)) / T)
    }

    override def toString: String = {
      "("+row+", "+col+") -> "+point
    }
  }

  protected class SomObsA(var numerator:Vector, var denominator: Double, val neuronId: Int) extends Serializable {
    def +(obs: SomObsA): SomObsA = {
      numerator += obs.numerator
      denominator += obs.denominator
      this
    }

    def compute = numerator / denominator

    override def toString = numerator.toString()+" : "+denominator.toString
  }
}
