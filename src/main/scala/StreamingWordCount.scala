import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.storage.StorageLevel
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.commons.codec.StringDecoder
//import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object StreamingWordCount {

  def main(args: Array[String]): Unit = {
    // 1. spark配置对象
    val conf = new SparkConf().setMaster("local[4]").setAppName("StreamingWordCount")
    val context = new SparkContext(conf)
    val ssc = new StreamingContext(context, Seconds(5))
    // 2. 定义kafka的参数
    val brokers = "localhost:9092"
    val topics = Array("wordcount-input")
    val group = "spark-streaming"
    // 3. 将kafka参数映射为map
    val kafkaParams = Map[String, String](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.GROUP_ID_CONFIG -> group,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
    )
    // 4. 通过kafkaUtil创建KafkaStream
    val kafkastream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    // 对kafkaDStream做计算，返回的每一个RDD都是 org.apache.kafka.clients.consumer.ConsumerRecord<K,V> 对象
    kafkastream.foreachRDD(_.foreach(println))

    // 启动 SparkStreaming
    ssc.start()
    ssc.awaitTermination()




  }

}
