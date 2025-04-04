package SparkDemos


import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.streaming.{DataStreamWriter, DataStreamWriter, StreamingQuery, Trigger}
// 下面的OutputMode 属于 spark-catalyst 依赖，而不是 spark-sql 依赖
import org.apache.spark.sql.streaming.{OutputMode}

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
      .setMaster("local")
    // SparkSQL的入口
    val spark = SparkSession.builder.config(conf).getOrCreate()
    import spark.implicits._

    // ----------------- 读取流数据 -----------------
    // --- socket 流 ---
    //val lines = spark.readStream
    //  .format("socket")
    //  .option("host", "localhost")
    //  .option("port", 8900)
    //  .load()
    // --- 文件流 ---
    // 必须是文件夹路径，不能直接指定文件
    val file_dir = "./src/main/resources/hadoop_data/wordcount_input"
    //val lines = spark.readStream.format("text").load(file_dir)
    // 下面的 text 其实是上面的快捷方法
    val lines: DataFrame = spark.readStream.text(file_dir)

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
      .awaitTermination(timeoutMs = 1000)

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

    // 如果要执行多个查询，可以在最后使用这个方法，注意，此方法没有参数，不能接受 timeoutMs = 1000 * 10 之类的
    spark.streams.awaitAnyTermination()

    spark.stop()
  }

}
