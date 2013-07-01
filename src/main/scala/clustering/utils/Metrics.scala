package clustering.utils

import clustering.AbstractModel
import spark.RDD
import spark.SparkContext._

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 01/07/13
 * Time: 17:17
 */
object Metrics {
  def purity(model: AbstractModel, dataset: RDD[NamedVector]): Double = {
    val sumAffectedDatas = dataset.map(d => ((model.findClosestPrototype(d).id, d.cls), 1))
      .reduceByKey{case (sum1, sum2) => sum1+sum2}

    val maxByCluster = sumAffectedDatas.map(sa => (sa._1._1, sa._2))
      .reduceByKey{case (sum1, sum2) => sum1.max(sum2) }
      .map(_._2)
      .collect()

    maxByCluster.sum / dataset.count().toDouble
  }
}
