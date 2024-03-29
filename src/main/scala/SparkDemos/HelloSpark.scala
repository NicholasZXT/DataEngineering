package SparkDemos

import org.apache.spark.{SparkConf, SparkContext}

object HelloSpark {

  def main(args: Array[String]): Unit = {
    println("Hello Spark")
    val conf = new SparkConf()
      .setAppName("Hello Spark")
      .set("spark.driver.memory", "1g")
      .set("spark.driver.cores", "1")
      .set("spark.executor.instances", "2")
      .set("spark.executor.cores", "2")
      .set("spark.executor.memory", "2g")
      .set("spark.default.parallelism", "2")
      .setMaster("local")
    val sc = new SparkContext(conf)
    // sc.setLogLevel("INFO")
    //sc.setLogLevel("WARN")

    val rdd1 = sc.parallelize(List(1, 2, 3, 4, 5, 6), numSlices = 3)
    println("partition num: " + rdd1.partitions.length)
    rdd1.foreach(println)

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
  def foreach_partition_fun(index: Int, iter: Iterator[Int]): Iterator[String] = {
    // 第一个参数是分区数，第二个是该分区元素的迭代器（不能是 Iterable）
    // 函数的返回值必须也是一个迭代器，所以不能使用 foreach
    iter.map(x => "[partID:" + index + ", val: " + x + "]")
  }

}
