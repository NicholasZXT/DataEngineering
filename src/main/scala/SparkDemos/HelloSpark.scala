package SparkDemos

import java.nio.file.{Path, Paths, Files}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

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
    sc.setLogLevel("WARN")

    //val valueRdd: RDD[Int] = sc.parallelize(List(1, 2, 3, 4, 5, 6), numSlices = 3)
    val valueRdd: RDD[Int] = sc.parallelize(1 to 10, numSlices = 3)
    //println("partition num: " + valueRdd.partitions.length)
    //valueRdd.foreach(println)

    // 从文本读取数据，转成 pairRdd
    val current_dir: Path = Paths.get(System.getProperty("user.dir"))
    //println(s"current_dir '${current_dir}' exist: ${Files.exists(current_dir)}.")
    val file_name: Path = Paths.get("src", "main", "resources", "hadoop_data", "wordcount_input", "wordcount.txt")
    val file_path: Path = current_dir.resolve(file_name)
    //println(s"file_path '${file_path}' exist: ${Files.exists(file_path)}.")
    val wordRdd: RDD[String] = sc.textFile(file_path.toString)
    //wordRdd.foreach(println)

    val wordPairRdd: RDD[(String, Int)] = wordRdd.flatMap(_.split(" ")).map(word => (word, 1))
    //wordPairRdd.foreach(println)

    // 演示 单值RDD 的 Transformation
    valueRddTransformation(valueRdd = valueRdd)

    // 演示 pairRDD 的 Transformation
    pairRddTransformation(wordPairRdd = wordPairRdd)

    sc.stop()

  }

  def valueRddTransformation(valueRdd: RDD[Int]): Unit = {
    // 分组转换
    val groupRdd: RDD[(Int, Iterable[Int])] = valueRdd.groupBy(x => x % 2)
    groupRdd.foreach(println)
  }


  def pairRddTransformation(wordPairRdd: RDD[(String, Int)]): Unit = {
  }

  // 用于 mapPartitionWithIndex 的函数
  def foreach_partition_fun(index: Int, iter: Iterator[Int]): Iterator[String] = {
    // 第一个参数是分区数，第二个是该分区元素的迭代器（不能是 Iterable）
    // 函数的返回值必须也是一个迭代器，所以不能使用 foreach
    iter.map(x => "[partID:" + index + ", val: " + x + "]")
  }

}
