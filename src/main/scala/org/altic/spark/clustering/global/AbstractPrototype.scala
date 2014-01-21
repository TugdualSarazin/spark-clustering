package org.altic.spark.clustering.global

import org.apache.spark.util.Vector

/**
 * Created with IntelliJ IDEA.
 * User: tug
 * Date: 14/06/13
 * Time: 12:42
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractPrototype(val id: Int, var _point: Vector) extends Serializable {
  def update(newPoint: Vector): Double = {
    val dist = _point.dist(newPoint)
    _point = newPoint
    dist
  }

  def dist(data: Vector) = _point.dist(data)

  def dist(prototype: AbstractPrototype) = _point.dist(prototype._point)
}
