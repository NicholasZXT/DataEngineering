import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession}


object HelloSpark {

  def main(args: Array[String]): Unit = {
    println("hello spark")
    val conf = new SparkConf()
      .setAppName("Hello Spark")
      .setMaster("local")
    val sc = new SparkContext(conf)
    // 从 SparkContext 创建 SparkSession
    val spark = SparkSession.builder.config(sc.getConf).getOrCreate()
    // sc.setLogLevel("INFO")
    sc.setLogLevel("WARN")

    val rdd1 = sc.parallelize(List(1,2,3,4,5,6), numSlices = 3)
    // rdd1.foreach(println)
    // rdd1.flatMap(_.split("")).map((_, 1)).
    //   reduceByKey(_+_, 1).
    //   saveAsTextFile("output")
    println("partition num: " + rdd1.partitions.size)

    // 从文本读取数据
    //val path1 = "hadoop_data/spark-test.csv"
    //val path2 = "hadoop_data/spark-README.md"
    //val rdd2 = sc.textFile(path1)
    //val rdd3 = sc.textFile(path2)
    //val df1 = spark.read.csv(path1)
    //val df2 = df1.rdd   // 这样返回的是 RDD[Row]
    // 这里读取的数据分区数 = 1, rdd 和 DF 都是一样的
    // println("partition num: " + rdd2.partitions.size)
    // println("df partition num: " + df1.rdd.partitions.size)
    // println("partition num: " + rdd3.partitions.size)
    // rdd2.foreach(println)

    // mapPartitionWithIndex 的使用
    //val rdd1_1 = rdd1.mapPartitionsWithIndex(foreach_partition_fun)
    //rdd1_1.foreach(println)

    sc.stop()

  }

  // 用于 mapPartitionWithIndex 的函数，
  def foreach_partition_fun(index:Int, iter:Iterator[Int]): Iterator[String] ={
    // 第一个参数是分区数，第二个是该分区元素的迭代器（不能是 Iterable）
    // 函数的返回值必须也是一个迭代器，所以不能使用 foreach
    iter.map(x => "[partID:" +  index + ", val: " + x + "]")
  }

}
