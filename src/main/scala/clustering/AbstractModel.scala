package clustering

import spark.util.Vector
import spark.RDD

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 14/06/13
 * Time: 12:34
 */
abstract class AbstractModel(val prototypes: Array[AbstractPrototype]) extends Serializable {
  def size = prototypes.size

  def findClosestPrototype(data: Vector): AbstractPrototype = {
    prototypes.minBy(proto => proto.dist(data))
  }

  def apply(i: Int) = prototypes(i)


  def assign(dataset: RDD[Vector]): RDD[(Int, Vector)] =  {
    dataset.map(d => (this.findClosestPrototype(d).id, d))
  }
  //def findClosestPrototype
}
