package SparkDemos

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.streaming.kafka010.{KafkaUtils, ConsumerStrategy}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import java.util.UUID

object SparkStreamingWithKafka {

  def main(args: Array[String]): Unit = {
    // 1. spark配置对象
    val conf = new SparkConf().setMaster("local[4]").setAppName("SparkStreamingWithKafka")
    val context = new SparkContext(conf)
    val ssc = new StreamingContext(context, Seconds(5))
    // 有状态操作需要配置检查点目录，本地目录要使用 file:/// 的形式
    ssc.checkpoint("./tmp/ssc/kafka-ck")

    // 2. 定义kafka的参数
    val brokers = "localhost:9092"
    val topics = Array("topic-1", "topic-2")
    //val group = "spark-streaming"
    val group = UUID.randomUUID().toString // 使用随机的group id，保证每次都是从topic的开始地方消费
    //println("group id: " + group)

    // 3. 将kafka参数映射为map
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],  // 推荐使用这种方式设置，不容易错
      ConsumerConfig.GROUP_ID_CONFIG -> group,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (true: java.lang.Boolean)
    )

    // 4. 订阅 topic
    val subscribe: ConsumerStrategy[String, String] = Subscribe[String, String](topics, kafkaParams)

    // 5. 通过kafkaUtil创建 Direct 的 KafkaStream，它是一个 spark 本身的 InputDStream 对象
    val kStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      subscribe
    )

    // 查看kafkaDStream的每一个记录，返回的每一个RDD都是 org.apache.kafka.clients.consumer.ConsumerRecord<K,V> 对象
    //kStream.foreachRDD(_.foreach(println)) // 这样打印的都是 ConsumerRecord<K,V> 对象
    kStream.foreachRDD(_.foreach(record => println("record.key: " + record.key + ", record.value: " + record.value)))

    // 启动 SparkStreaming
    ssc.start()
    ssc.awaitTermination()

  }

}
