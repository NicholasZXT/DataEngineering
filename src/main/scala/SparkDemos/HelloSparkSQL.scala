package SparkDemos

import org.apache.spark.{SparkConf, SparkContext}
// sql package 里常用的就是如下的对象/类了
import org.apache.spark.sql.{SparkSession, SaveMode, Column, ColumnName, DataFrame, Dataset, DataFrameReader, DataFrameWriter}
// 下面这个 functions 是一个 Object，封装了许多SQL函数
import org.apache.spark.sql.functions.{col, column, lit, expr, get_json_object}
// SparkSQL的类型申明，其中 DataType 是所有类型的基类 —— types 属于 spark-catalyst 依赖（不是 spark-sql 依赖）
import org.apache.spark.sql.types.{StructType, StructField, DataType, StringType, IntegerType, LongType, FloatType,
  DateType, MapType, ArrayType, BooleanType, BinaryType}

object HelloSparkSQL {

  def main(args: Array[String]): Unit = {
    println("Hello Spark SQL")
    val conf = new SparkConf()
      .setAppName("Hello Spark SQL")
      .set("spark.driver.memory", "1g")
      .set("spark.driver.cores", "1")
      .set("spark.executor.instances", "2")
      .set("spark.executor.cores", "2")
      .set("spark.executor.memory", "2g")
      .set("spark.default.parallelism", "2")
      .setMaster("local")
    // SparkSQL的入口
    val spark = SparkSession.builder.config(conf).getOrCreate()
    // 从 SparkContext 创建 SparkSession
    //val sc = new SparkContext(conf)
    //val spark = SparkSession.builder.config(sc.getConf).getOrCreate()

    spark.stop()

  }
}
