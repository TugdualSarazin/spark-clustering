package org.altic.spark.clustering

import org.apache.log4j.PropertyConfigurator
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.util.Vector
import org.altic.spark.clustering.utils.SparkReader
import org.altic.spark.clustering.bitm.{BiTM, Croeuc}
import org.altic.spark.clustering.som.SomTrainerB
import java.io.File

import utils.IO

/**
 * Company : Altic - LIPN
 * User: Tugdual Sarazin
 * Date: 07/01/14
 * Time: 12:30
 */
object UCIClustering extends App {
//object UCIClustering {

  def context() : SparkContext = {
    val prgName = this.getClass.getSimpleName
    if (args.length > 0) {
      // export SPARK_HOME=/home/tug/ScalaProjects/spark-0.8.1-incubating-bin-cdh4
      // export SPARK_RUN_JAR=/home/tug/ScalaProjects/spark-clustering/target/scala-2.9.3/spark-clustering-assembly-0.8.1-SNAPSHOT.jar
      // java -jar /home/tug/ScalaProjects/spark-clustering/target/scala-2.9.3/spark-clustering-assembly-0.8.1-SNAPSHOT.jar spark://localhost.localdomain:7077
      // scp -P 2822 /home/tug/ScalaProjects/spark-clustering/target/scala-2.9.3/spark-clustering-assembly-0.8.1-SNAPSHOT.jar tugdual@magi.univ-paris13.fr:/home/dist/tugdual/runSparkSlurm
      println("## "+args(0)+" ##")

      System.setProperty("spark.executor.memory", "4g")
      System.setProperty("spark.local.dir", "/tmp/spark")

      //new SparkContext(args(0), prgName, System.getenv("SPARK_HOME"), Seq(System.getenv("SPARK_RUN_JAR")))
      new SparkContext(args(0), prgName, System.getenv("SPARK_HOME"), SparkContext.jarOfObject(UCIClustering))
    } else {
      println("## LOCAL ##")
      //PropertyConfigurator.configure("conf/log4j.properties")
      new SparkContext("local", prgName)
    }
  }

  class Experience(val datasetDir: String, val name: String, val nbProtoRow: Int, val nbProtoCol: Int, val nbIter: Int) {
    def dir: String = datasetDir+"/"+name
    def dataPath: String = dir+"/"+name+".data"
    def nbTotProto: Int = nbProtoCol * nbProtoRow
  }

  PropertyConfigurator.configure("log4j.properties")
  val sc = context()

  val globalDatasetDir = "./data"
  val globalNbIter = 10
  val nbExp = 10


  // Note ; map height/width = round(((5*dlen)^0.54321)^0.5)
  //        dlen is the number of row of the dataset
  val experiences = Array(
    new Experience(globalDatasetDir, "Glass", 7, 7, globalNbIter),
    new Experience(globalDatasetDir, "CancerWpbcRet", 7, 7, globalNbIter),
    new Experience(globalDatasetDir, "SonarMines", 7, 7, globalNbIter),
    new Experience(globalDatasetDir, "Heart", 7, 7, globalNbIter),
    new Experience(globalDatasetDir, "Spectf1", 8, 8, globalNbIter),
    new Experience(globalDatasetDir, "MovementLibras", 8, 8, globalNbIter),
    new Experience(globalDatasetDir, "Breast", 9, 9, globalNbIter),
    new Experience(globalDatasetDir, "HorseColic", 7, 7, globalNbIter),
    new Experience(globalDatasetDir, "Isolet5", 11, 11, globalNbIter),
    new Experience(globalDatasetDir, "Waveform", 16, 16, globalNbIter),
    new Experience(globalDatasetDir, "LetterRecognition", 23, 23, globalNbIter),
    new Experience(globalDatasetDir, "Shuttle", 28, 28, globalNbIter),
    new Experience(globalDatasetDir, "Covtype", 57, 57, globalNbIter)
  )

  experiences.foreach{exp =>
    // Delete old results
    cleanResults(exp)

    // Read data from file
    println("\n***** READ : "+exp.name)
    val datas = SparkReader.parse(sc, exp.dataPath, " ").cache()

    for (numExp <- 1 to nbExp) {
      println("\n***** CROEUC : "+exp.name+" ("+numExp+"/"+nbExp+")")
      val croeuc = new Croeuc(exp.nbTotProto, datas)
      croeuc.training(exp.nbIter)

      println("\n***** BiTM : "+exp.name+" ("+numExp+"/"+nbExp+")")
      val bitm = new BiTM(exp.nbProtoRow, exp.nbProtoCol, datas)
      bitm.training(exp.nbIter)

      println("\n***** SOM : "+exp.name+" ("+numExp+"/"+nbExp+")")
      val som = new SomTrainerB
      val somOptions = Map("clustering.som.nbrow" -> exp.nbProtoRow.toString, "clustering.som.nbcol" -> exp.nbProtoCol.toString)
      val somConvergeDist = -0.1
      som.training(datas.asInstanceOf[RDD[Vector]], somOptions, exp.nbIter, somConvergeDist)

      // Save results
      croeuc.affectations(datas).map(d => d._1+" "+d._2).saveAsTextFile(exp.dir+"/"+exp.name+".clustering.croeuc_"+exp.nbTotProto+"-"+numExp)
      bitm.affectations(datas).map(d => d._1+" "+d._2).saveAsTextFile(exp.dir+"/"+exp.name+".clustering.bitm_"+exp.nbProtoRow+"x"+exp.nbProtoCol+"-"+numExp)
      som.affectations(datas).map(d => d._1+" "+d._2).saveAsTextFile(exp.dir+"/"+exp.name+".clustering.som_"+exp.nbProtoRow+"x"+exp.nbProtoCol+"-"+numExp)
    }
  }

  def cleanResults(exp: Experience) {
    val resultDirs = new File(exp.dir).listFiles.filter(_.getName.startsWith(exp.name+".clustering"))
    resultDirs.foreach(IO.delete)

  }
}
