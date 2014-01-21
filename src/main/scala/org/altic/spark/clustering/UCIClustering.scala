package org.altic.spark.clustering

import org.apache.log4j.PropertyConfigurator
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.util.Vector
import org.altic.spark.clustering.utils.SparkReader
import org.altic.spark.clustering.bitm.{BiTM, Croeuc}
import org.altic.spark.clustering.som.SomTrainerB

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 07/01/14
 * Time: 12:30
 */
//object UCIClustering extends App {
object UCIClustering {
  PropertyConfigurator.configure("log4j.properties")
  val sc = new SparkContext("local", this.getClass.getSimpleName)

  val datasetDir = "/home/tug/ScalaProjects/som_datasets/"
  val datasetFiles = Array("Breast.data", "glass.data", "Heart.data", "HorseColic.data", /*"isolet5.data",*/
    "LungCancer.data", "movement_libras.data", "sonar.data", "SPECTF.data")
  //val datasetFiles = Array("isolet5.data")
  //val datasetFiles = Array("glass.data")

  val nbRowSOM = 5
  val nbColSOM = 5
  val nbIter = 10

  datasetFiles.foreach{file =>
    println("\n***** "+file)
    // Read data from file
    val datas = SparkReader.parse(sc, datasetDir+file, ";")

    //println("****************\n**** CROEUC ****\n****************")
    val croeuc = new Croeuc(nbRowSOM * nbColSOM, datas)
    croeuc.training(nbIter)

    //println("****************\n***** BITM *****\n****************")
    val bitm = new BiTM(nbRowSOM, nbColSOM, datas)
    bitm.training(nbIter)

    //println("****************\n***** SOM  *****\n****************")
    val som = new SomTrainerB
    val somOptions = Map("clustering.som.nbrow" -> nbRowSOM.toString, "clustering.som.nbcol" -> nbColSOM.toString)
    val somConvergeDist = -0.1
    som.training(datas.asInstanceOf[RDD[Vector]], somOptions, nbIter, somConvergeDist)

    println("** Croeuc purity : "+croeuc.purity(datas))
    println("** BiTM purity : "+bitm.purity(datas))
    println("** SOM purity : "+som.purity(datas))

    //println("** Croeuc quant errors : "+croeuc.quantErrors.reverse.mkString("(", ",", ")"))
    //println("** BiTM quant errors : "+bitm.quantErrors.reverse.mkString("(", ",", ")"))
    //println("** SOM quant errors : "+som.quantErrors.reverse.mkString("(", ",", ")"))
  }
}