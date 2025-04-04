package SparkDemos

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream, InputDStream}
import org.apache.spark.rdd.RDD
import scala.collection.mutable

object HelloSparkStreaming {
  //val checkpointDirectory = "hdfs://path/to/ck"
  val checkpointDirectory = "./tmp/ssc/ck"
  val batchDuration: Duration = Seconds(2)

  // 用于创建 StreamingContext 的函数，配合检查点恢复使用
  def createStreamingContext(conf: SparkConf): StreamingContext = {
    val ssc = new StreamingContext(conf = conf, batchDuration = this.batchDuration)
    ssc.checkpoint(this.checkpointDirectory)
    ssc
  }

  def main(args: Array[String]): Unit = {
    // --------- 初始化 Spark StreamingContext -----------
    // 初始化 SparkConf
    val conf = new SparkConf().setMaster("local[4]").setAppName("HelloSparkStreaming")
    // 有两种途径：
    // 第一种：初始化一个全新的 StreamingContext
    // 初始化 StreamingContext, 设置每批的时间间隔
    val ssc = new StreamingContext(conf = conf, batchDuration = this.batchDuration)
    // 开启检查点+容错的设置
    ssc.checkpoint(this.checkpointDirectory)

    // 第二种：从检查点目录中恢复一个 StreamingContext，提供容错恢复功能
    // 运行时，如果检查点目录不存在，就调用第二个的工厂函数创建一个新的 StreamingContext，否则就从检查点恢复
    //val ssc = StreamingContext.getOrCreate(checkpointDirectory, () => createStreamingContext(conf))

    // --------- 读取流数据 -----------
    // 从Socket流读取数据：监听本地 socket 端口，返回的是 ReceiverInputStream
    // 注意，Socket流启动之前必须先运行 nc -lk 8900（Windows使用Nmap工具的 ncat -lp 8900）
    //val lineStream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 8900)

    // 从 RDD 队列读取流数据，用于调试非常方便 —— 不过 queueStream 不支持检查点
    val rddQueue = new mutable.Queue[RDD[String]]()
    val lineStream: InputDStream[String] = ssc.queueStream(rddQueue,oneAtATime = true)
    // 下面这个调用，可以放到 ssc.start() 之后
    //mockRddQueue(ssc.sparkContext, rddQueue)
    lineStream.cache()
    //lineStream.foreachRDD(_.foreach(println))
    //println("-------- lineStream.print ------")  // 这个println没啥作用
    // 下面的print方法会首先打印一个时间戳
    lineStream.print(10)

    // 检查点的生成间隔需要在具体的流之后设置
    //lineStream.checkpoint(Duration(1000*10))
    val wordStream: DStream[(String, Int)] = lineStream.flatMap(_.split(" ")).map((_, 1))
    wordStream.foreachRDD(
      rdd => {
        println("-------- wordStream --------")
        rdd.foreach(println)
      }
    )

    // --------- 流数据转换 -----------
    // 无状态转换
    //transformationWithoutState(ssc, wordStream)
    // 有状态转换
    //transformationWithState(ssc, wordStream)
    // 窗口相关操作
    transformationWithWindow(ssc, wordStream)

    // 启动流式计算环境
    ssc.start()

    mockRddQueue(ssc.sparkContext, rddQueue)

    // 必须要await，等待作业完成，因为会在另一个线程中执行，主线程需要进行等待
    //ssc.awaitTermination()
    // 可以使用下面的方式进行调试，它只会等待指定时间（单位是毫秒）
    ssc.awaitTerminationOrTimeout(1000*30)

  }

  def mockRddQueue(spark: SparkContext, queue: mutable.Queue[RDD[String]]): Unit = {
    val rdd1 = spark.makeRDD(Array("hello world", "hello spark", "hello scala"))
    val rdd2 = spark.makeRDD(Array("hello scala", "hello streaming", "hello spark"))
    val rdd3 = spark.makeRDD(Array("hello scala", "hello streaming", "hello spark"))
    val rdd4 = spark.makeRDD(Array("scala spark", "streaming", "spark scala"))
    val rdds = Array(rdd1, rdd2, rdd3, rdd4)
    for (rdd <- rdds) {
      queue += rdd
      Thread.sleep(1000*1)
    }
  }

  def transformationWithoutState(ssc: StreamingContext, wordStream: DStream[(String, Int)]): Unit = {
    // --------- map操作 --------------
    val mapDStream = wordStream.map(word => (word._1, word._2 * 10))
    // 不带时间
    //mapDStream.foreachRDD(_.foreach(println))
    mapDStream.foreachRDD(
      rdd => {
        println("----- mapDStream without time -------")
        rdd.foreach(println)
      }
    )
    // 带有时间
    mapDStream.foreachRDD(
      (rdd, time) => {
        println(s"----- mapDStream with time: $time -------")
        rdd.foreach(println)
      }
    )

    // --------- filter操作 --------------
    val filterDStream = wordStream.filter(word => !word._1.contains("hello"))
    filterDStream.foreachRDD(
      rdd => {
        println("----- filterDStream-------")
        rdd.foreach(println)
      }
    )

    // --------- 实现无状态转化的word count，统计每个时间批次内的word count --------------
    // 实现方式1
    val wc1 = wordStream.reduceByKey(_+_)
    wc1.foreachRDD(
      rdd => {
        println("-------- word count 1 ---------")
        rdd.foreach(println)
      }
    )
    // 实现方式2
    val wc2 = wordStream.groupByKey().map({
      case (word, cnt) => (word, cnt.sum)
    })
    wc2.foreachRDD(
      rdd => {
        println("-------- word count 2 ---------")
        rdd.foreach(println)
      }
    )
  }

  def transformationWithState(ssc: StreamingContext, wordStream: DStream[(String, Int)]): Unit = {
    // ----------- updateByKey 使用 ------------------
    // 允许你根据每个键（key）的历史状态和当前批次的新数据，计算出该键的新状态。
    // 它的参数是一个函数：updateFunc: (Seq[V], Option[S]) => Option[S]，定义了如何根据当前批次的数据和之前的状态计算新的状态
    //   - Seq[V]：当前批次中与某个键相关的所有新值（可以是空序列）
    //   - Option[S]：该键之前的旧状态（如果是第一次出现，则为 None）
    //   - Option[S]：返回值，表示该键的新状态。如果返回 None，则表示删除该键的状态
    // 算子返回一个新的 DStream[(K, S)]
    // 以下实现 有状态的 word count，此时必须要开启检查点
    // 首先定义状态更新函数
    val updateCntFunc = (values: Seq[Int], prevCount: Option[Int]) => {
      // values 是当前微批次中 某个key 的数据序列，prevCount 是保存该 key 状态的变量
      val prevCnt = prevCount.getOrElse(0)
      val sumCnt = values.sum
      val sumAcc = prevCnt + sumCnt
      Some(sumAcc)
    }
    val wcState = wordStream.updateStateByKey[Int](updateCntFunc)
    wcState.foreachRDD(
      rdd => {
        println("-------- wcState ---------")
        rdd.foreach(println)
      }
    )
    val wcResult = wcState.groupByKey().map(word => (word._1, word._2.sum))
    wcResult.foreachRDD(
      rdd => {
        println("-------- wcResult ---------")
        rdd.foreach(println)
      }
    )
  }

  /**
   * 窗口函数相关操作
   * @param ssc
   * @param wordStream
   */
  def transformationWithWindow(ssc: StreamingContext, wordStream: DStream[(String, Int)]): Unit = {
    // -------- window 算子 --------
    val windowStream: DStream[(String, Int)] = wordStream.window(windowDuration = Seconds(2), slideDuration = Seconds(2))
    windowStream.foreachRDD(
      rdd => {
        println("-------- windowStream ---------")
        rdd.foreach(println)
      }
    )

    // -------- countByWindow 算子 --------
    val countWindowStream: DStream[Long] = wordStream.countByWindow(windowDuration = Seconds(2), slideDuration = Seconds(2))
    countWindowStream.foreachRDD(
      rdd => {
        println("-------- countWindowStream ---------")
        rdd.foreach(println)
      }
    )

    // -------- reduceByWindow 算子 --------
    // 签名为：reduceByWindow(func, windowLength, slideInterval)
    // 其中 func 为 (T, T) => T，定义如何对两个元素进行聚合操作
    val reduceFunc = (prev: (String, Int), cur: (String, Int)) => {
      (prev._1 + "-" + cur._1, prev._2 + cur._2)
    }
    val reduceWindowStream: DStream[(String, Int)] = wordStream.reduceByWindow(
      reduceFunc,
      windowDuration = Seconds(2),
      slideDuration = Seconds(2)
    )
    reduceWindowStream.foreachRDD(
      rdd => {
        println("-------- reduceWindowStream ---------")
        rdd.foreach(println)
      }
    )

    // -------- reduceByKeyAndWindow 算子 --------
    // 签名为：reduceByKeyAndWindow(func, windowLength, slideInterval, [numTasks])
    val reduceKeyWindowStream: DStream[(String, Int)] = wordStream.reduceByKeyAndWindow(
      (preValue: Int, curValue: Int) => preValue + curValue,
      windowDuration = Seconds(2),
      slideDuration = Seconds(2)
    )
    reduceKeyWindowStream.foreachRDD(
      rdd => {
        println("-------- reduceKeyWindowStream ---------")
        rdd.foreach(println)
      }
    )
    // -------- countByValueAndWindow 算子 --------
    // 签名为：countByValueAndWindow(windowLength,slideInterval, [numTasks])
  }

}
