package clustering

import akka.util.Duration
import spark.RDD
import spark.util.Vector
import java.util.concurrent.TimeUnit._

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 14/06/13
 * Time: 12:31
 */
trait AbstractTrainer extends Serializable {
  private var _it = 0
  def getLastIt = _it

  private var _converge = 1.0
  def getLastConvergence = _converge

  private var _trainingDuration = Duration.Undefined
  def getLastTrainingDuration = _trainingDuration

  protected def initModel(dataset: RDD[Vector], modelOptions: Map[String, String])

  protected def trainingIteration(dataset: RDD[Vector], currentIteration: Int, maxIteration: Int): Double

  protected def getModel: AbstractModel

  final def training(dataset: RDD[Vector],
                     modelOptions: Map[String, String] = Map.empty,
                     maxIteration: Int = 100,
                     endConvergeDistance: Double = 0.001): AbstractModel = {

    val datasetSize = dataset.count()

    val startLearningTime = System.currentTimeMillis()

    val model = initModel(dataset, modelOptions)

    _it = 0
    _converge = 1.0

    while (_converge > endConvergeDistance && _it < maxIteration) {

      // Training iteration
      val sumConvergence = trainingIteration(dataset, _it, maxIteration)

      // process convergence
      _converge = sumConvergence / datasetSize
      _it += 1
    }
    _trainingDuration = Duration(System.currentTimeMillis() - startLearningTime, MILLISECONDS)

    // return the model
    getModel
  }
}
