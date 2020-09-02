import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


object HelloSpark {

  def main(args: Array[String]): Unit = {

    println("hello spark")

    val conf = new SparkConf()
      .setAppName("Hello Spark")
      .setMaster("local")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val rdd1:RDD[String] = sc.textFile("input/")
    rdd1.foreach(println)

//    rdd1.flatMap(_.split("")).map((_, 1)).
//      reduceByKey(_+_, 1).
//      saveAsTextFile("output")

    sc.stop()

  }

}
