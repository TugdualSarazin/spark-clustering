package org.altic.spark.clustering.utils

import util.Random
import org.apache.spark.util.Vector


/**
 * Created with IntelliJ IDEA.
 * User: tug
 * Date: 27/03/13
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
object DataGenerator {
  private val rand = new Random

  private case class DModel(A: Double, B: Double) {
    def gen =  A * rand.nextDouble() + B
  }

  private case class PModel(cls: Int, dmodels: Array[DModel]) {
    def genVector = new Vector(dmodels.map(_.gen))
    def genNamedVector = new NamedVector(dmodels.map(_.gen), cls)
  }

  private def PModel2D(cls: Int, A: Double, B: Double, C: Double) = PModel(cls, Array(DModel(A, B), DModel(A, C)))

  private def PModelND(cls: Int, dims: Int, A: Double, B: Double) = PModel(cls, Array.fill(dims)(DModel(A, B)))

  class SModel(N: Int, pmodels: Array[PModel]) {
    private def nextVector(i: Int) = pmodels(rand.nextInt(pmodels.size)).genVector
    private def nextNamedVector(i: Int) = pmodels(rand.nextInt(pmodels.size)).genNamedVector
    def getVector = Array.tabulate(N)(nextVector)
    def getNamedVector = Array.tabulate(N)(nextNamedVector)
  }
  val CLS_1 = 1
  val CLS_2 = 2
  val CLS_3 = 3
  val CLS_4 = 4

  def genH2Dims(N: Int) = new SModel(N, Array(
    PModel2D(CLS_1, 1, 1, 1),
    PModel2D(CLS_1, 1, 1, 2),
    PModel2D(CLS_1, 1, 1, 3),
    PModel2D(CLS_1, 1, 2, 2),
    PModel2D(CLS_1, 1, 3, 1),
    PModel2D(CLS_1, 1, 3, 2),
    PModel2D(CLS_1, 1, 3, 3)
  ))

  def gen2Cls2Dims(N: Int) = new SModel(N, Array(
    PModel2D(CLS_1, 1, 1, 1),
    PModel2D(CLS_2, 2, 2, 2)
  ))

  def gen2ClsNDims(N: Int, dims: Int) = new SModel(N, Array(
    PModelND(CLS_1, dims, 1, 1),
    PModelND(CLS_2, dims, 2, 2)
  ))
}

/*class DataGenerator(val N: Int) {

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
    //new RandPoint("cls1", 1, 1, 1)
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
}*/
