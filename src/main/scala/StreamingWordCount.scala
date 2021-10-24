import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.storage.StorageLevel
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.commons.codec.StringDecoder
//import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import java.util.UUID

object StreamingWordCount {

  def main(args: Array[String]): Unit = {
    // 1. spark配置对象
    val conf = new SparkConf().setMaster("local[4]").setAppName("StreamingWordCount")
    val context = new SparkContext(conf)
    val ssc = new StreamingContext(context, Seconds(5))
    // 有状态操作需要配置检查点目录，本地目录要使用 file:/// 的形式
    ssc.checkpoint("file:///C:\\BigDataEnv\\tmp")
    // 2. 定义kafka的参数
    val brokers = "localhost:9092"
    val topics = Array("wordcount-input")
    //val group = "spark-streaming"
    val group = UUID.randomUUID().toString  // 使用随机的group id，保证每次都是从topic的开始地方消费
    println("group id: " + group)
    // 3. 将kafka参数映射为map
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.GROUP_ID_CONFIG -> group,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: java.lang.Boolean)
    )
    // 4. 通过kafkaUtil创建KafkaStream，它是一个 spark 本身的 InputDStream 对象
    val kafkastream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    // 查看kafkaDStream的每一个记录，返回的每一个RDD都是 org.apache.kafka.clients.consumer.ConsumerRecord<K,V> 对象
    //kafkastream.foreachRDD(_.foreach(println)) // 这样打印的都是 ConsumerRecord<K,V> 对象
    //kafkastream.foreachRDD(_.foreach(record => println("record.key: " + record.key + ", record.value: " + record.value)))

    // 5.1 实现 无状态转化的 word count
    val line_split = kafkastream.flatMap(record => record.value().split(" ")).map(word => (word, 1))
    //line_split.foreachRDD(_.foreach(println))
    //val wc = line_split.groupByKey().map(res => (res._1, res._2.sum))
    //wc.foreachRDD(_.foreach(println))

    // 5.2 实现 有状态的 word count
    // 状态更新函数
    val updateFunc = (values:Seq[Int], oldstate:Option[Int]) => {
      val old_cnt = oldstate.getOrElse(0)
      val add_cnt = values.sum
      val new_cnt = old_cnt + add_cnt
      Some(new_cnt)
    }
    val wc_state = line_split.updateStateByKey[Int](updateFunc)
    //wc_state.foreachRDD(_.foreach(println))
    val wc_result = wc_state.groupByKey().map(res => (res._1, res._2.sum))
    wc_result.foreachRDD(_.foreach(println))

    // 启动 SparkStreaming
    ssc.start()
    ssc.awaitTermination()

  }

}
