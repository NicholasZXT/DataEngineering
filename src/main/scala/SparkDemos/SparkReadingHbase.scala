package SparkDemos

import scala.collection.mutable.{Buffer, Map}
import scala.collection.JavaConverters.asScalaBufferConverter
import java.util.{List => JavaList}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.CellUtil
import org.apache.hadoop.hbase.Cell
import utils.HBaseUtils

/**
 * 下面这种访问 HBase 数据的方式，应该是直接读的 HDFS上的 HBase 表数据，没有经过 Hbase 的 RegionServer，
 * 所以也不能实现 HBase 客户端的 Scan, Get 等操作.
 */
object SparkReadingHbase extends App {

  val conf = new SparkConf()
    .setAppName("Reading HBase data Demo")
    .set("spark.driver.memory", "1g")
    .set("spark.driver.cores", "1")
    .set("spark.executor.instances", "2")
    .set("spark.executor.cores", "2")
    .set("spark.executor.memory", "2g")
    .set("spark.default.parallelism", "2")
    .setMaster("local")
  val sc = new SparkContext(conf)
  // sc.setLogLevel("WARN")

  val tableName = "hbase_table"
  val quorum = "10.8.6.185"
  val port = "2128"
  val hhase: HBaseUtils = new HBaseUtils(quorum)
  val hbase_conf: Configuration = hhase.config
  hbase_conf.set(TableInputFormat.INPUT_TABLE, tableName)

  // 下面的参数中：
  //  fClass = TableInputFormat 对应的是数据在HDFS上的存储格式
  //  kClass = org.apache.hadoop.hbase.io.ImmutableBytesWritable 指定的是 fClass 中 key 对应的 class
  //  vClass = org.apache.hadoop.hbase.client.Result 指定的是 fClass 中 value 对应的 class
  // 最终得到的是一个 RDD[(K, V)]
  val hbaseRDD = sc.newAPIHadoopRDD(hbase_conf, classOf[TableInputFormat],
    classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable], classOf[org.apache.hadoop.hbase.client.Result])

  // 看一下得到的 RDD[(K, V)] 是个什么样子
  // 并将其转换成 RDD[(rowkey, {qualifier:family -> value})] 的形式
  hbaseRDD.map(
    row => {
      // row 是一个 (k, v) 元组，k 是 fClass 对象，v 是 vClass 对象
      val (k, v) = (row._1, row._2)
      println("kClass: " + k.getClass.getName)
      println("vClass: " + v.getClass.getName)
      println("k.toString: ", k.toString)  // 这个 key 好像没啥用

      // v 是 HBase 的 Result 类对象，封装了所有的数据，包括rowkey
      val rowkey: String = Bytes.toString(v.getRow)
      val record = Map[String, String]()
      val cells: Buffer[Cell] = v.listCells().asScala  // 这里将 java.utils.List 转成了 scala.collection.mutable.Buffer
      for(cell <- cells){
        // val rowkey  = Bytes.toString(CellUtil.cloneRow(cell));  // 每个 cell 里也有 rowkey
        val qualifier  = Bytes.toString(CellUtil.cloneQualifier(cell));
        val family = Bytes.toString(CellUtil.cloneFamily(cell));
        val value = Bytes.toString(CellUtil.cloneValue(cell));
        record(qualifier + ":" +family) = value
      }
      (rowkey, record)
    }
  ).take(1).foreach(println)

  //hbaseRDD.map(row => {
  //  val result = row._2
  //  val key = Bytes.toString(result.getRow)
  //  val value = Bytes.toString(result.getValue("cf:txtAppNum".getBytes, "cf:txtTitle".getBytes))
  //  (key, value)
  //}).take(5).foreach(println)

  sc.stop()

}

