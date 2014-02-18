package org.altic.spark.clustering.utils

import util.Random
import org.apache.spark.util.Vector
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.Array


/**
 * Created with IntelliJ IDEA.
 * User: tug
 * Date: 27/03/13
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
object DataGen {

  class Center(val cls: Int, val rayon: Double, val elements: Array[Double]) extends Serializable {
    def this(cls: Int, dims: Int, a: Double, b: Double, rayon: Double) = this(cls, rayon, Array.fill(dims)(new Random(42).nextGaussian() * a + b))
  }

  /*def gen(sc: SparkContext,
                        numPoints: Int,
                        nbCls: Int,
                        dims: Int,
                        numPartitions: Int = 2): RDD[NamedVector] =
  {
    val CLS_1 = 1
    val CLS_2 = 2
    val CLS_3 = 3
    val CLS_4 = 4

    val centers = Array(
      new Center(CLS_1, dims, 1.0, 1.0),
      new Center(CLS_2, dims, 2.0, 2.0)
    )

    sc.parallelize(0 until numPoints, numPartitions).map{ idx =>
      val center = centers(idx % nbCls)
      val rand = new Random(42 + idx)
      center.elements.map(el => el + center.rayon * rand.nextGaussian())
      new NamedVector(Array.tabulate(d)(i => center(i) + rand2.nextGaussian()), 1)
    }
  }*/

  def generate(sc: SparkContext,
                        numPoints: Int,
                        nbCls: Int,
                        d: Int,
                        numPartitions: Int = 2): RDD[NamedVector] =
  {
    // First, generate some centers
    val rand = new Random(42)
    val r = 1.0
    val centers = Array.fill(nbCls)(Array.fill(d)(rand.nextGaussian() * r))
    // Then generate points around each center
    sc.parallelize(0 until numPoints, numPartitions).map{ idx =>
      val cls = idx % nbCls
      val center = centers(cls)
      val rand2 = new Random(42 + idx)
      new NamedVector(Array.tabulate(d)(i => center(i) + rand2.nextGaussian()), cls)
    }
  }
}

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
