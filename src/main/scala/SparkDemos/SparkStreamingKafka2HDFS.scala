package SparkDemos

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}
import org.apache.spark.streaming.{Seconds, StreamingContext, Duration}
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010.{ConsumerStrategy, KafkaUtils}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.{Assign, Subscribe}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import com.alibaba.fastjson.{JSON, JSONObject}
import java.util.Date
import java.text.SimpleDateFormat
import scala.collection.mutable.ArrayBuffer
import SparkDemos.MessageBean

/**
 * 展示如何使用 SparkStreaming 消费 kafka 中的JSON消息，写入HDFS
 */
class SparkStreamingKafka2HDFS {
  val seconds = 10  // 每批次时间间隔
  val ck_path = "./tmp/message-ck"

  def createStreamingContext(): StreamingContext = {
    val conf = new SparkConf().setMaster("local[4]").setAppName("SparkStreamingKafka2HDFS")
    val ssc = new StreamingContext(conf, batchDuration = Seconds(this.seconds))
    ssc.checkpoint(ck_path)
    ssc
  }

  def main(args: Array[String]): Unit = {
    // 创建 SparkStreaming 环境入口
    // 本地调试配置
    val conf = new SparkConf().setMaster("local[4]").setAppName("SparkStreamingKafka2HDFS")
    val ssc = new StreamingContext(conf, batchDuration = Seconds(this.seconds))
    // 检查点容错重启配置 // TODO 这个检查点容错，重启的时候似乎不生效，有待研究
    //val ssc = StreamingContext.getOrCreate(ck_path, createStreamingContext _)

    // 创建 SparkSQL 的环境入口
    val spark = SparkSession.builder().config(ssc.sparkContext.getConf).getOrCreate()

    val outpath = "./tmp/message-data"
    val brokers = "localhost:9092"
    val topic = "topic-1"
    val topicPartition = new TopicPartition(topic, 0)
    val group = "topic-1-consumer"
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.GROUP_ID_CONFIG -> group,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
      ConsumerConfig.MAX_POLL_RECORDS_CONFIG -> "500",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (false: java.lang.Boolean)
    )

    // 消费策略配置
    // 消费topic的所有partition
    //val consumerStrategy: ConsumerStrategy[String, String] = Subscribe[String, String](Seq(topic), kafkaParams)
    // 指定消费topic的某个partition
    val consumerStrategy: ConsumerStrategy[String, String] = Assign[String, String](Seq(topicPartition), kafkaParams)

    // 连接Kafka Stream
    val kStream = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, consumerStrategy)
    //kStream.cache()
    kStream.checkpoint(Duration(1000*this.seconds))  // 检查点生成间隔

    val message_schema = new StructType()
      .add(StructField("mid", IntegerType, nullable=false))
      .add(StructField("content", StringType, nullable=true))
      .add(StructField("datetime", StringType, nullable=true))

    //kStream.foreachRDD(_.foreach(println))
    kStream.foreachRDD(rdd => {
      //println(rdd.name)
      val ms_rdd = rdd.mapPartitions(par => {
        val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
        val array = new ArrayBuffer[Row]()
        par.foreach(record => {
          //println(record)
          val topic = record.topic()
          val partition = record.partition()
          val offset = record.offset()
          val timestamp = record.timestamp()
          val value = record.value()
          val dt = dateFormat.format(new Date())
          //println(value)
          val bean = JSON.parseObject(value, classOf[MessageBean])
          //println(bean)
          val row = Row(bean.mid, bean.content, bean.datetime, topic, partition, offset, timestamp, dt)
          array.append(row)
        })
        //println(array.length)
        array.iterator
      })

      //val cnt = ms_rdd.count()
      //println(s"****** $cnt ******")
      //ms_rdd.take(5).foreach(println)
      // 转成DataFrame
      val ms_df = spark.createDataFrame(ms_rdd, message_schema)
      //ms_df.printSchema()
      //ms_df.show(5, truncate = false)
      ms_df.coalesce(10).write.mode(SaveMode.Append).partitionBy("dt").parquet(outpath)
    })

    ssc.start()
    ssc.awaitTermination()
    //ssc.awaitTerminationOrTimeout(1000*11)
  }

}
