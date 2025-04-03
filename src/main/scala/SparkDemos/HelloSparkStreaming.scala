package SparkDemos

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.rdd.RDD
import scala.collection.mutable

object HelloSparkStreaming {
  //val checkpointDirectory = "hdfs://path/to/ck"
  val checkpointDirectory = "./tmp/ssc/ck"

  // 用于创建 StreamingContext 的函数，配合检查点恢复使用
  def createStreamingContext(conf: SparkConf): StreamingContext = {
    val ssc = new StreamingContext(conf = conf, batchDuration = Seconds(5))
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
    val ssc = new StreamingContext(conf = conf, batchDuration = Seconds(5))
    // 开启检查点+容错的设置
    ssc.checkpoint(checkpointDirectory)

    // 第二种：从检查点目录中恢复一个 StreamingContext，提供容错恢复功能
    // 运行时，如果检查点目录不存在，就调用第二个的工厂函数创建一个新的 StreamingContext，否则就从检查点恢复
    //val ssc = StreamingContext.getOrCreate(checkpointDirectory, () => createStreamingContext(conf))

    // --------- 读取流数据 -----------
    // 从Socket流读取数据：监听本地 socket 端口，统计word count
    // 这里返回的是 ReceiverInputStream
    //val lineStream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 8900)

    // 从 RDD 队列读取流数据，用于调试非常方便
    val rddQueue = new mutable.Queue[RDD[String]]()
    val lineStream = ssc.queueStream(rddQueue,oneAtATime = true)
    // 下面这个调用，可以放到 ssc.start() 之后
    fakeRddQueue(ssc.sparkContext, rddQueue)
    lineStream.foreachRDD(_.foreach(println))

    // 检查点的生成间隔需要在具体的流之后设置
    //lineStream.checkpoint(Duration(1000*10))
    val wordStream: DStream[(String, Int)] = lineStream.flatMap(_.split(" ")).map((_, 1))
    wordStream.foreachRDD(_.foreach(println))

    // 3.1 实现无状态转化的word count，统计每个时间批次内的word count
    //val wc = wordStream.reduceByKey(_+_)  // 实现方式1
    //val wc = wordStream.groupByKey().map(res => (res._1, res._2.sum))  // 实现方式2
    //wc.foreachRDD(_.foreach(println))
    // 3.2 实现 有状态的 word count，此时必须要开启检查点
    // 状态更新函数
    val updateFunc = (values: Seq[Int], oldState: Option[Int]) => {
      // values 是当前微批次中的数据序列，oldState 是保存状态的变量
      val old_cnt = oldState.getOrElse(0)
      val add_cnt = values.sum
      val new_cnt = old_cnt + add_cnt
      Some(new_cnt)
    }
    val wc_state = wordStream.updateStateByKey[Int](updateFunc)
    //wc_state.foreachRDD(_.foreach(println))
    val wc_result = wc_state.groupByKey().map(res => (res._1, res._2.sum))
    //wc_result.foreachRDD(_.foreach(println))

    // 启动流式计算环境，
    // 注意，如果是 Socket流，那么启动之前必须先运行 nc -lk 8900（Windows使用Nmap工具的 ncat -lp 8900）
    ssc.start()
    // 必须要await，等待作业完成，因为会在另一个线程中执行，主线程需要进行等待
    //ssc.awaitTermination()
    // 可以使用下面的方式进行调试，它只会等待指定时间（单位是毫秒）
    ssc.awaitTerminationOrTimeout(1000*60)
  }

  def fakeRddQueue(spark: SparkContext, queue: mutable.Queue[RDD[String]]): Unit = {
    val rdd1 = spark.makeRDD(Array("hello world", "hello spark", "hello scala"))
    val rdd2 = spark.makeRDD(Array("hello scala", "hello streaming", "hello spark"))
    val rdd3 = spark.makeRDD(Array("hello scala", "hello streaming", "hello spark"))
    val rdds = Array(rdd1, rdd2, rdd3)
    for (rdd <- rdds) {
      queue += rdd
      Thread.sleep(1000*1)
    }
  }

}
