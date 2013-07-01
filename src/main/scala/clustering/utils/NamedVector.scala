package clustering.utils

import spark.util.Vector

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 27/03/13
 * Time: 17:07
 */
class NamedVector(elements: Array[Double], val cls: String) extends Vector(elements) with Serializable {
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
