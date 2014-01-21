package org.altic.spark.clustering.bitm

import org.apache.spark.SparkContext
import org.apache.spark.util.Vector
import org.apache.spark.rdd.RDD
import org.apache.log4j.PropertyConfigurator
import org.altic.spark.clustering.utils.DataGenerator
import org.altic.spark.clustering.som.SomTrainerB

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 06/01/14
 * Time: 12:50
 */
object RunAllClustering extends App {
//object RunAllClustering {
  def context() : SparkContext = {
    val prgName = this.getClass.getSimpleName
    if (args.length > 0) {
      // export SPARK_HOME=/home/tug/ScalaProjects/spark-0.8.1-incubating-bin-cdh4
      // export SPARTAKUS_JAR=/home/tug/ScalaProjects/Spartakus/target/scala-2.9.3/spartakus-core-assembly-0.8-SNAPSHOT.jar
      // java -jar /home/tug/ScalaProjects/Spartakus/target/scala-2.9.3/spartakus-core-assembly-0.8-SNAPSHOT.jar spark://localhost.localdomain:7077
      // scp -P 2822 /home/tug/ScalaProjects/Spartakus/target/scala-2.9.3/spartakus-core-assembly-0.8-SNAPSHOT.jar tugdual@magi.univ-paris13.fr:/home/dist/tugdual/runSparkSlurm
      println("## "+args(0)+" ##")

      System.setProperty("SPARK_MEM", "8g")
      System.setProperty("spark.local.dir", "/tmp/spark")

      new SparkContext(args(0), prgName, System.getenv("SPARK_HOME"), Seq(System.getenv("SPARK_RUN_JAR")))
    } else {
      println("## LOCAL ##")
      PropertyConfigurator.configure("conf/log4j.conf")
      new SparkContext("local", prgName)
    }
  }

  val nbRowSOM = 5
  val nbColSOM = 5
  val nbIter = 3
  val dataNbObs = 400
  //val dataNbObs = 40
  val dataNbVars = 10
  //val dataNbVars = 2

  val sc = context()

  // Data generation
  val arrDatas = DataGenerator.gen2ClsNDims(dataNbObs, dataNbVars).getNamedVector
  //val arrDatas = Array(new NamedVector(Array(1.0, 1.1), 1), new NamedVector(Array(1.5, 1.6), 1), new NamedVector(Array(2.0, 2.1), 2))
  //val arrDatas = Array.tabulate(dataNbObs)(i => new Vector(Array.tabulate(dataNbVars)(j => i*20+j+10)))
  //val arrDatas = Array.tabulate(dataNbObs)(i => new AffectedVector(1, Array.tabulate(dataNbVars)(j => 1)))
  val datas = sc.parallelize(arrDatas, 1)


  println("****************\n**** CROEUC ****\n****************")
  val croeuc = new Croeuc(nbRowSOM * nbColSOM, datas)
  croeuc.training(nbIter)

  println("****************\n***** BITM *****\n****************")
  val bitm = new BiTM(nbRowSOM, nbColSOM, datas)
  bitm.training(nbIter)

  println("****************\n***** SOM  *****\n****************")
  val som = new SomTrainerB
  val somOptions = Map("clustering.som.nbrow" -> nbRowSOM.toString, "clustering.som.nbcol" -> nbColSOM.toString)
  val somConvergeDist = -0.1
  som.training(datas.asInstanceOf[RDD[Vector]], somOptions, nbIter, somConvergeDist)
}