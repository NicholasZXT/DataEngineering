package SparkDemos

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{StreamingContext, Seconds}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}

object HelloSparkStreaming {

  def main(args: Array[String]): Unit = {
    // 1. 初始化 SparkConf
    val conf = new SparkConf().setMaster("local[4]").setAppName("HelloSparkStreaming")
    // 2. 初始化 StreamingContext, 设置每批的时间间隔
    val ssc = new StreamingContext(conf = conf, batchDuration = Seconds(5))
    ssc.checkpoint(checkpointDirectory)
    // 开启检查点+容错的设置
    // 运行时，如果检查点目录不存在，就调用第二个的工厂函数创建一个新的 StreamingContext，否则就从检查点恢复一个
    // 这里使用了 _ 来将方法转成函数
    //val ssc = StreamingContext.getOrCreate(checkpointDirectory, createStreamingContext _)

    // 3. 监听本地 socket 端口，统计word count
    // 这里返回的是 ReceiverInputStream
    val lineStream: ReceiverInputDStream[String] = ssc.socketTextStream("localhost", 8900)
    val wordStream: DStream[(String, Int)] = lineStream.flatMap(_.split(" ")).map((_, 1))
    //wordStream.foreachRDD(_.foreach(println))
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
    wc_result.foreachRDD(_.foreach(println))

    // 4. 启动流式计算环境，注意，启动之前必须先运行 nc -lk 8900
    ssc.start()
    // 必须要await，等待作业完成，因为会在另一个线程中执行，主线程需要进行等待
    ssc.awaitTermination()
    //ssc.awaitTerminationOrTimeout(60)
  }

  //val checkpointDirectory = "hdfs://path/to/ck"
  val checkpointDirectory = "./tmp/ssc/ck"

  // 用于创建 StreamingContext 的工厂函数
  def createStreamingContext(): StreamingContext = {
    val conf = new SparkConf().setMaster("local[4]").setAppName("HelloSparkStreaming")
    val ssc = new StreamingContext(conf = conf, batchDuration = Seconds(5))
    ssc.checkpoint(checkpointDirectory)
    ssc
  }

}
