package SparkDemos

import java.nio.file.{Path, Paths, Files}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD

/**
 * 演示 Spark Core 的使用
 */
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
    valueRddTransformation(sc: SparkContext, valueRdd = valueRdd)

    // 演示 pairRDD 的 Transformation
    pairRddTransformation(sc: SparkContext, wordPairRdd = wordPairRdd)

    sc.stop()

  }

  def valueRddTransformation(sc: SparkContext, valueRdd: RDD[Int]): Unit = {
    // map
    println("--------- mapRdd ---------")
    val mapRdd: RDD[Int] = valueRdd.map(x => x * 2)
    mapRdd.foreach(println)
    // 过滤
    println("--------- filterRdd ---------")
    val filterRdd: RDD[Int] = valueRdd.filter(x => x % 2 == 0)
    filterRdd.foreach(println)
    // 排序
    println("--------- sortRdd ---------")
    val sortRdd: RDD[Int] = valueRdd.sortBy(x => x, ascending = false)
    sortRdd.foreach(println)
    // 分组转换
    println("--------- groupRdd ---------")
    val groupRdd: RDD[(Int, Iterable[Int])] = valueRdd.groupBy(x => x % 2)
    groupRdd.foreach(println)
  }


  def pairRddTransformation(sc: SparkContext, wordPairRdd: RDD[(String, Int)]): Unit = {
    // mapValues
    println("--------- mapValuesRdd ---------")
    val mapValuesRdd: RDD[(String, Int)] = wordPairRdd.mapValues(x => x * 2)
    mapValuesRdd.foreach(println)

    // groupByKey
    println("--------- groupByKeyRdd ---------")
    val groupByKeyRdd: RDD[(String, Iterable[Int])] = wordPairRdd.groupByKey()
    groupByKeyRdd.foreach(println)
    println("--------- groupByKeyRdd + Sum ---------")
    val groupByKeySum: RDD[(String, Int)] = groupByKeyRdd.map(group => (group._1, group._2.sum))
    groupByKeySum.foreach(println)


    // reduceByKey
    println("--------- reduceByKeyRdd ---------")
    //val reduceByKeyRdd: RDD[(String, Int)] = wordPairRdd.reduceByKey(_ + _)
    val reduceByKeyRdd: RDD[(String, Int)] = wordPairRdd.reduceByKey((x, y) => x + y)
    reduceByKeyRdd.foreach(println)


    // aggregateByKey，这个算子比较复杂，参数签名为（注意有两组括号）：
    // (zeroValue: U, partitioner: Partitioner)(seqOp: (U, V) => U, combOp: (U, U) => U)
    // 第1组参数:
    //   zeroValue: U, 每个分区（不是每个key）的初始值，会在每个分区中都进行一次初始化，建议使用不可变对象
    //   partitioner: Partitioner, 分区器，可选参数，默认为 HashPartitioner
    // 第2组参数:
    //   seqOp: (U, V) => U, 定义如何在 每个分区 内部将当前分区中的 Value 聚合到中间结果中，U 是中间结果类型（累积器），V 是当前分区中的 Value 类型
    //   combOp: (U, U) => U, 多个分区的计算结果如何合并成最终结果，两个参数分别是来自不同分区的中间结果（类型为 U）
    // 返回值：RDD[(K, U)]，每个 key 对应于一个聚合后的结果（类型为U）
    // 下面取每个 key 的最大值，然后相加
    val pairRdd: RDD[(String, Int)] = sc.parallelize(
      List(("a", 3), ("a", 2), ("a", 4), ("b", 3), ("b", 4), ("b", 5), ("c", 5), ("c", 8), ("c", 2)),
      numSlices = 2
    )
    val aggregateByKeyRdd: RDD[(String, Int)] = pairRdd.aggregateByKey(0)(
      (key, value) => math.max(key,value),
      (kv1, kv2) => kv1 + kv2
    )
    println("--------- aggregateByKeyRdd ---------")
    // 打印每个分区的数据
    pairRdd.mapPartitionsWithIndex((index, iterator) => {
      val partitionData = iterator.toList
      println(s"Partition $index: ${partitionData.mkString(", ")}")
      Iterator((index, partitionData))
    }).count()
    println(">>>>>>")
    aggregateByKeyRdd.foreach(println)


    // combineByKey[C]，这个算子也比较复杂，参数签名为：
    //   createCombiner: V => C, 创建每个key的初始值
    //     输入是当前分区中的 某个key 的第一个Value（V），输出是一个新的 Combiner（类型为 C）
    //   mergeValue: (C, V) => C, 如何将当前分区中的 相同 Key 的 Value 合并到已有的 Combiner 中
    //     第一个参数是现有的 Combiner（类型为 C），第二个参数是当前分区中的 Value（类型为 V）
    //   mergeCombiners: (C, C) => C, 定义如何将不同分区的 Combiner 合并成最终结果
    //     两个参数分别是来自不同分区的 Combiner（类型为 C）
    //   partitioner: Partitioner,
    //   mapSideCombine: Boolean = true
    // 返回值：RDD[(K, C)]，每个 key 对应一个聚合后的结果（类型为C）
    // 工作流程分为3个阶段：
    //   1. 创建初始聚合器：当遇到某个 Key 的第一个 Value 时，调用 createCombiner 创建一个初始的聚合器（Combiner）
    //   2. 局部聚合（Partition 内部聚合）：在每个分区中，使用 mergeValue 将当前分区中的 相同 Key 的其他 Value 合并到已有的 Combiner 中
    //   3. 全局聚合（Partition 之间合并）：使用 mergeCombiners 将所有分区的 Combiner 合并成最终结果。
    // 下面的例子是计算每个 key 的均值
    val combineByKeyRdd: RDD[(String, (Int, Int))] = pairRdd.combineByKey(
      // 1. 初始聚合器：每个分区内，对于每个 key 的第一个 Value，转换成 (value, 1) 的元组，1 是初始计数，表示这个key出现了1次
      // 这里得到的初始化 combiner 类型是 (Int, Int)
      (value: Int) => (value, 1),
      // 2. 局部聚合：每个分区内，合并所有key的Value，并计数，得到 (sum, count)
      // accumulator 是分区内已有的 combiner —— 也就是 1 中初始化的返回类型，value 是每个key的 value
      (accumulator: (Int, Int), value: Int) => (accumulator._1 + value, accumulator._2 + 1),
      // 3. 全局聚合：合并所有分区的 (sum, count)，得到最终结果
      (acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2)
    )
    println("--------- combineByKeyRdd ---------")
    combineByKeyRdd.foreach(println)
    // 计算均值
    println(">>>> key.mean by mapValues")
    combineByKeyRdd.mapValues({
      // 这里使用了模式匹配
      case (sum, count) => sum.toDouble / count
    }).foreach(println)
    // 或者使用 map 算子
    println(">>>> key.mean by map")
    combineByKeyRdd.map({
      case (key, (sum, count)) => (key, sum.toDouble / count)
    }).foreach(println)
  }

  // 用于 mapPartitionWithIndex 的函数
  def foreach_partition_fun(index: Int, iter: Iterator[Int]): Iterator[String] = {
    // 第一个参数是分区数，第二个是该分区元素的迭代器（不能是 Iterable）
    // 函数的返回值必须也是一个迭代器，所以不能使用 foreach
    iter.map(x => "[partID:" + index + ", val: " + x + "]")
  }

}
