package clustering.utils

import util.Random
import spark.util.Vector


/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 27/03/13
 * Time: 17:07
 */
object DataGenerator {
  private val rand = new Random

  private case class DModel(A: Double, B: Double) {
    def gen =  A * rand.nextDouble() + B
  }

  private case class PModel(name: String, dmodels: Array[DModel]) {
    def genVector = new Vector(dmodels.map(_.gen))
    def genNamedVector = new NamedVector(dmodels.map(_.gen), name)
  }

  private def PModel2D(name: String, A: Double, B: Double, C: Double) = PModel(name, Array(DModel(A, B), DModel(A, C)))

  private def PModelND(name: String, dims: Int, A: Double, B: Double) = PModel(name, Array.fill(dims)(DModel(A, B)))

  private case class SModel(N: Int, pmodels: Array[PModel]) {
    private def nextVector(i: Int) = pmodels(rand.nextInt(pmodels.size)).genVector
    private def nextNamedVector(i: Int) = pmodels(rand.nextInt(pmodels.size)).genNamedVector
    def genVector = Array.tabulate(N)(nextVector)
    def genNamedVector = Array.tabulate(N)(nextNamedVector)
  }

  def vectorH2D(N: Int) = SModel(N, Array(
    PModel2D("cls1", 1, 1, 1),
    PModel2D("cls1", 1, 1, 2),
    PModel2D("cls1", 1, 1, 3),
    PModel2D("cls1", 1, 2, 2),
    PModel2D("cls1", 1, 3, 1),
    PModel2D("cls1", 1, 3, 2),
    PModel2D("cls1", 1, 3, 3)
  )).genVector

  def vector2Cls2D(N: Int) = SModel(N, Array(
    PModel2D("cls1", 1, 1, 1),
    PModel2D("cls2", 2, 2, 2)
  )).genVector

  def vector2ClsND(N: Int, dims: Int) = SModel(N, Array(
    PModelND("cls1", dims, 1, 1),
    PModelND("cls2", dims, 2, 2)
  )).genVector
}

class DataGenerator(val N: Int) {

  private val rand = new Random

  private class RandPoint(val clsName: String, A: Double, B: Double, C: Double) {
    def gen = Array(A * rand.nextDouble() + B, A * rand.nextDouble() + C)
  }

  private val randPoints = Array[RandPoint](
    new RandPoint("cls1", 1, 1, 1),
    new RandPoint("cls1", 1, 1, 2),
    new RandPoint("cls1", 1, 1, 3),
    new RandPoint("cls1", 1, 2, 2),
    new RandPoint("cls1", 1, 3, 1),
    new RandPoint("cls1", 1, 3, 2),
    new RandPoint("cls1", 1, 3, 3)
    //new RandPoint("cls1", 1, 1, 1),
    //new RandPoint("cls2", 2, 2, 2)
  )

  private def nextPoint = randPoints(rand.nextInt(randPoints.size))

  private def nextNamedVecor(i: Int): NamedVector = {
    val rPoint = nextPoint
    new NamedVector(rPoint.gen, rPoint.clsName)
  }

  private def nextVector(i: Int) = new Vector(nextPoint.gen)

  def genNamedVectors = Array.tabulate(N)(nextNamedVecor)
  def genVectors = Array.tabulate(N)(nextVector)
}
