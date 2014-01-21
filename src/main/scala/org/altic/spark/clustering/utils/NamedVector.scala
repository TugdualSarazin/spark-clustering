package org.altic.spark.clustering.utils

import org.apache.spark.util.Vector

/**
 * Created with IntelliJ IDEA.
 * User: tug
 * Date: 27/03/13
 * Time: 17:07
 * To change this template use File | Settings | File Templates.
 */
class NamedVector(elements: Array[Double], val cls: Int) extends Vector(elements) with Serializable {
  override def toString(): String = {
    "#"+cls+" "+super.toString()
  }
  def toJSON(clusterId: Int): String = {
    var str = new StringBuilder
    str append "{"
    for (i <- 0 until elements.length) {
      str append "attr"+i+":"+elements(i)+", "
    }
    str append "cls:\""+cls+"\", "
    str append "clusterId:"+clusterId
    str append "}\n"
    str.toString()
  }
}