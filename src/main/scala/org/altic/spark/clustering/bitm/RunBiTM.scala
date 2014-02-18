package org.altic.spark.clustering.bitm

import org.apache.log4j.PropertyConfigurator
import org.apache.spark.SparkContext
import org.apache.spark.util.Vector
import org.altic.spark.clustering.utils.{NamedVector, DataGenerator}

//object RunBiTM extends App {
object RunBiTM {
  val nbRowSOM = 2
  val nbColSOM = 1
  val nbIter = 2
  val dataNbObs = 4
  val dataNbVars = 2
  val outputPath = "/home/tug/workspaceWeb/somVizu/somDataset.js"

  class AffectedVector(var rowNeuronId: Int, elements: Array[Double]) extends Vector(elements) {
    def indices = elements.indices
  }

  def parseData(line: String): AffectedVector = {
    // TODO : Remplacer 0 par rand
    new AffectedVector(1, line.split(' ').map(_.toDouble))
  }

  PropertyConfigurator.configure("conf/log4j.conf")
  val sc = new SparkContext("local", "RunBiTM")

  // Read data from file
  //val lines = sc.textFile("/home/tug/ScalaProjects/spark/kmeans_data.txt")
  //val datas = lines.map(parseData _).cache()

  //val datas = sc.parallelize(Array.fill(1)(parseData("1")), 1)


  val arrDatas = DataGenerator.gen2ClsNDims(dataNbObs, dataNbVars).getNamedVector
  //val arrDatas = Array(Vector(Array(1.0, 1.1)), Vector(Array(2.0, 2.1)))
  //val arrDatas = Array.tabulate(dataNbObs)(i => new Vector(Array.tabulate(dataNbVars)(j => i*20+j+10)))
  //val arrDatas = Array.tabulate(dataNbObs)(i => new AffectedVector(1, Array.tabulate(dataNbVars)(j => 1)))
  //val datas = sc.parallelize(arrDatas, 6)

  val datas = sc.textFile("/home/tug/ScalaProjects/spark-clustering/pyImg/lena/lena.data").map(line => new NamedVector(line.split("\t").map(_.toDouble), 0))
  //val datas = sc.textFile("/home/tug/pyImg/randImg.4colors.data").map(line => new Vector(line.split("\t").map(_.toDouble)))


  // Initialisation du model
  //val model = new BiTM(nbRowSOM, nbColSOM, datas)
  val model = new Croeuc(nbRowSOM * nbColSOM, datas)

  model.training(nbIter)

  val affData = model.affectation(datas)

  affData.map(_.mkString("\t")).saveAsTextFile("/home/tug/pyImg/spark.output")

  sys.exit()
}