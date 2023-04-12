import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession}

object HelloSparkSQL {

  def main(args: Array[String]): Unit = {
    println("Hello SparkSQL")
    val conf = new SparkConf()
      .setAppName("Hello SparkSQL")
      .set("spark.driver.memory", "1g")
      .set("spark.driver.cores", "1")
      .set("spark.executor.instances", "2")
      .set("spark.executor.cores", "2")
      .set("spark.executor.memory", "2g")
      .set("spark.default.parallelism", "2")
      .setMaster("local")
    // SparkSQL的入口
    val spark = SparkSession.builder.config(conf).getOrCreate()
    // 从 SparkContext 创建 SparkSession
    //val sc = new SparkContext(conf)
    //val spark = SparkSession.builder.config(sc.getConf).getOrCreate()

    spark.stop()

  }
}
