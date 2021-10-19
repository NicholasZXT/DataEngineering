import org.apache.spark.{SparkConf}
import org.apache.spark.streaming.{StreamingContext,Seconds}
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.streaming.kafka010.{KafkaUtils}

object StreamingWordCount {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StreamingWordCount").setMaster("local[*]")
    val ssc = new StreamingContext(conf = conf, batchDuration = Seconds(5))
    //定义kafka的参数
    val brokers = "localhost:9092"
    val topic = "wordcount-input"
    val group = None
    // 将kafak参数映射为map
    val kafkaParams = Map[String, String](ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer"
    )



  }

}
