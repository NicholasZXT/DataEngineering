package SparkDemos

import org.apache.spark.{SparkConf, SparkContext}
// sql package 里常用的是如下的对象/类
import org.apache.spark.sql.{SparkSession, Column, ColumnName, DataFrame, Dataset, DataFrameReader, DataFrameWriter, SaveMode}
// 下面的类/对象和 SparkSession 提供的隐式转换有关，一般不需要显式导入
//import org.apache.spark.sql.{SQLImplicits, LowPrioritySQLImplicits, DatasetHolder}
// 下面这个 functions 是一个 Object，封装了许多SQL函数，包括UDF —— 经常使用
import org.apache.spark.sql.functions.{col, column, lit, expr, get_json_object}

// ----- 下面是 org.apache.spark-catalyst 包里的内容 -----
// SparkSQL的类型申明，其中 DataType 是所有类型的基类 —— types 属于 spark-catalyst 依赖（不是 spark-sql 依赖）
import org.apache.spark.sql.types.{
  StructType, StructField, DataType, StringType, IntegerType, LongType, FloatType,
  DoubleType, DateType, MapType, ArrayType, BooleanType, BinaryType
}
// SparkSQL DataFrame 每一行是一个 Row 对象，不过 Row 本身是一个 Trait，具体实现是 GenericRowWithSchema 等
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.expressions.{GenericRow, GenericRowWithSchema, JoinedRow, UnsafeRow}

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
      .set("spark.ui.port", "8080")
      .setMaster("local")
    // SparkSQL的入口
    val spark = SparkSession.builder.config(conf).getOrCreate()
    // 从 SparkContext 创建 SparkSession
    //val sc = new SparkContext(conf)
    //val spark = SparkSession.builder.config(sc.getConf).getOrCreate()

    // SparkSession 提供的隐式转换
    import spark.implicits._

    val data = Seq(
      (1, 18, "male", 90, "A"),
      (2, 24, "female", 80, "B"),
      (3, 28, "male", 50, "D"),
    )
    val df = spark.createDataFrame(data).toDF("id", "age", "gender", "score", "grade")
    //df.printSchema()
    //df.rdd.foreach(row => println(row.getClass.getName))
    // org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema

    val row = Row(1, 18, "male", 90, "A")
    println(row.getClass.getName)  // org.apache.spark.sql.catalyst.expressions.GenericRow
    println(row)

    spark.stop()

  }
}
