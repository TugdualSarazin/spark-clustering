package org.altic.spark.clustering.bitm

import scala.math.exp

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 06/01/14
 * Time: 12:08
 */
trait TopoFactor extends Serializable {
  def gen(maxIter:Int, currentIter:Int, nbNeurons: Int): Array[Double]
}

object BiTMTopoFactor extends TopoFactor {
  def gen(maxIter:Int, currentIter:Int, nbNeurons: Int) = {
    val T:Double = (maxIter - currentIter + 1) / maxIter.toDouble
    //val T = 0.9
    Array.tabulate(nbNeurons*2)(dist => exp(dist / T))
  }
}

object CroeucTopoFactor extends TopoFactor {
  def gen(maxIter: Int, currentIter: Int, nbNeurons: Int): Array[Double] = {
    val T:Double = (maxIter - currentIter + 1) / maxIter.toDouble
    //val T = 0.9
    Array.tabulate(nbNeurons*2)(i => if (i == 0) exp(i / T) else Double.MaxValue)
  }
}
