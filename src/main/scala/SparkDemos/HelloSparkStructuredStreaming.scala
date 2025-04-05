package SparkDemos

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, DataFrame, Dataset, Row}
import org.apache.spark.sql.streaming.{DataStreamWriter, StreamingQuery, Trigger}
import org.apache.spark.sql.functions.{window, to_timestamp}
// 下面的OutputMode 属于 spark-catalyst 依赖，而不是 spark-sql 依赖
import org.apache.spark.sql.streaming.{OutputMode}
import java.sql.Timestamp

object HelloSparkStructuredStreaming {

  def main(args: Array[String]): Unit = {
    println(">>>>>> Hello Spark StructuredStreaming")
    val conf = new SparkConf()
      .setAppName("Hello Spark StructuredStreaming")
      .set("spark.driver.memory", "1g")
      .set("spark.driver.cores", "1")
      .set("spark.executor.instances", "2")
      .set("spark.executor.cores", "2")
      .set("spark.executor.memory", "2g")
      .set("spark.default.parallelism", "2")
      .set("spark.ui.port", "8080")
      .setMaster("local")
    // SparkSQL的入口
    val spark = SparkSession.builder.config(conf).getOrCreate()
    import spark.implicits._

    // ----------------- 读取流数据 -----------------
    // --- 文件流 ---
    // 必须是文件夹路径，不能直接指定文件
    val file_dir = "./src/main/resources/hadoop_data/wordcount_input"
    //val lines: DataFrame = spark.readStream.format("text").load(file_dir)
    // 下面的 text 其实是上面的快捷方法
    //val lines: DataFrame = spark.readStream.text(file_dir)

    // --- socket 流 ---
    val lines: DataFrame = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9700)
      .option("includeTimestamp", value = true)
      .load()

    // Stream下，DataFrame的 show 方法不能使用
    //lines.show(5)
    // 可以通过下面的方式构造一个 query 来查看
    lines.writeStream.outputMode("append")
      .format("console")
      //.option("numRows", value = 6)
      //.option("truncate", value = false)
      // 下面的 options 方法可以用 Map[String, String] 的方式接收多个参数，不过 value 需要以 String 类型传递
      .options(Map("numRows" -> "5", "truncate" -> "false"))
      // 可以为此查询起一个名称，查询名称会显示在 Spark UI 和日志中，用于标识该查询，不过有点可惜的是这个名称不会在 console 输出中显示
      .queryName("lines stream")
      // 只有执行 start() 了，query 才会输出
      .start()
      // 这里一旦 await，就会阻塞，导致执行不到后面的代码，如果要执行多个查询，需要在最后使用 spark.streams.awaitAnyTermination()
      //.awaitTermination()
      // 也可以指定 await 的时间，不过还是不太推荐，因为这样只会输出一次
      //.awaitTermination(timeoutMs = 1000)

    // ----------------- 处理流数据 -----------------
    // wordCount 基本演示
    wordCount(spark, lines)

    // 时间戳+Window+Watermark操作
    wordCountWithTime(spark, lines)

    // 如果要执行多个查询，可以在最后使用这个方法，注意，此方法没有参数，不能接受 timeoutMs = 1000 * 10 之类的
    spark.streams.awaitAnyTermination()

    spark.stop()
  }

  def wordCount(spark: SparkSession, lines: DataFrame): Unit = {
    import spark.implicits._
    val words = lines.as[String].flatMap(_.split(" "))
    // 设置查询时，得到的还是 DataStreamWriter 对象
    val wordShow: DataStreamWriter[String] = words.writeStream
      .outputMode("append")
      .format("console")
      .queryName("words stream")
    // DataStreamWriter.start() 方法返回的就是 StreamingQuery 对象了，此时查询就可以执行了
    val wordShowQuery: StreamingQuery = wordShow.start()
    //wordShowQuery.awaitTermination()

    val wordCounts = words.groupBy("value").count()
    wordCounts.writeStream
      .outputMode("complete")
      .format("console")
      .queryName("wordCounts Stream")
      .start()
    //.awaitTermination()
  }

  def wordCountWithTime(spark: SparkSession, lines: DataFrame): Unit = {
    import spark.implicits._
    //lines.printSchema()
    val wordsWithTime: Dataset[(Timestamp, String, String)] = lines.flatMap(
      (line: Row) => {
        val lineContent = line.getString(0)
        val timestamp = line.getTimestamp(1)
        val lineSplits = lineContent.split(",")
        //println(s"lineContent: $lineContent, timestamp: $timestamp")
        //println("lineSplits: " + lineSplits.mkString("Array(", ", ", ")"))
        val eventTime = lineSplits(0)
        val words = lineSplits(1).split(" ")
        words.map((word: String) => (timestamp, eventTime, word))
      }
    ).toDF("timestamp", "eventTime", "word")
      .as[(Timestamp, String, String)]
    //wordsWithTime.printSchema()
    wordsWithTime.writeStream
      .outputMode("append")
      .format("console")
      .queryName("wordsWithTime stream")
      .options(Map("numRows" -> "5", "truncate" -> "false"))
      //.start()

    val wordCountWindow: Dataset[Row] = wordsWithTime
      .groupBy(
        // 根据实际时间戳设置窗口
        //window($"timestamp", "4 seconds", "2 seconds"),
        // 根据事件时间设置窗口
        window($"eventTime", "10 seconds", "10 seconds"),
        $"word"
      )
      .count()
      .orderBy($"window")
    wordCountWindow.writeStream
      .outputMode("complete")  // 这里不能使用 append 模式
      .format("console")
      .queryName("wordCountWindow stream")
      .options(Map("numRows" -> "5", "truncate" -> "false"))
      //.start()

    val wordCountWatermark: Dataset[Row] = wordsWithTime
      // 水印字段必须是时间戳格式
      .withColumn("eventTime", to_timestamp($"eventTime"))
      // 设置水印
      .withWatermark("eventTime", "20 seconds")
      .groupBy(
        // 根据事件时间设置窗口
        window($"eventTime", "10 seconds", "10 seconds"),
        $"word"
      )
      .count()
    wordCountWatermark.writeStream
      .outputMode("update")  // 这里不能使用 append 模式
      .trigger(Trigger.ProcessingTime(1000))
      .format("console")
      .queryName("wordCountWatermark stream")
      .options(Map("numRows" -> "5", "truncate" -> "false"))
      .start()
  }

}
