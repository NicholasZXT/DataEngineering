import org.apache.spark.{SparkConf, SparkContext}


object HelloSpark {

  def main(args: Array[String]): Unit = {

    println("hello spark")

    val conf = new SparkConf().setMaster("local").setAppName("Hello Spark")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val rdd1 = sc.textFile("input/")
    rdd1.collect()

    rdd1.flatMap(_.split("")).map((_, 1)).
      reduceByKey(_+_, 1).
      saveAsTextFile("output")

    sc.stop()

  }

}
