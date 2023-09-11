package SparkDemos

import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.sql.types.{DataType, StructType, StructField, StringType, MapType, ArrayType}
import org.apache.spark.sql.functions.{col, get_json_object, from_json, explode, map_keys, map_values, element_at}


/**
 * Spark SQL 解析 JSON
 */
object ParsingJson extends App {

  val spark = SparkSession.builder()
    .appName("SparkSQL Json Parsing")
    .master("local[2]")
    .config("spark.local.dir", "C:\\Users\\Drivi\\Java-Projects\\DataEngineering\\tmp")
    .getOrCreate()

  // 引入 spark 中的隐式转换
  import spark.implicits._

  // 手动创建JSON结构的数据
  val data1 = Seq(Row("stu1", "{\"student\": {\"name\": \"zhang\", \"age\": 24}}"))
  val schema1 = StructType(List(StructField("id", StringType), StructField("student", StringType)))
  val data2 = Seq(Row("grade1", "{\"students\": [{\"name\": \"zhang\", \"age\": 26}, {\"name\": \"zhou\", \"age\": 23}]}"))
  val schema2 = StructType(List(StructField("id", StringType), StructField("students", StringType)))
  val df1 = spark.createDataFrame(spark.sparkContext.makeRDD(data1), schema1)
  val df2 = spark.createDataFrame(spark.sparkContext.makeRDD(data2), schema2)
  df1.show(false)
  df2.show(false)

  // 使用 get_json_object
  val df1_get = df1.withColumn("name", get_json_object($"student", "$.student.name"))
    .withColumn("age", get_json_object($"student", "$.student.age"))
  val df2_get = df2.withColumn("names", get_json_object($"students", "$.students.name"))
    .withColumn("ages", get_json_object($"students", "$.students.age"))
    .withColumn("name1", get_json_object($"students", "$.students[0].name"))
    .withColumn("age1", get_json_object($"students", "$.students[0].age"))
    .withColumn("name2", get_json_object($"students", "$.students[1].name"))
    .withColumn("age2", get_json_object($"students", "$.students[1].age"))
  //df1_get.show(false)
  //df2_get.show(false)

  // 使用 from_json
  // 第1种方式，使用 MapType 指定 schema
  val map_type_1 = MapType(StringType, MapType(StringType, StringType))
  val df1_from = df1.withColumn("stu_map", from_json($"student", map_type_1))
    .withColumn("stu.value", element_at($"stu_map", "student"))
    .withColumn("stu.value.name", element_at($"`stu.value`", "name"))
    .withColumn("stu.value.age", element_at($"`stu.value`", "age"))
  //df1_from.printSchema()
  //df1_from.show(false)
  val map_type_2 = MapType(StringType, ArrayType(MapType(StringType, StringType)))
  val df2_from = df2.withColumn("stu_map", from_json($"students", map_type_2))
    .withColumn("stu.values", element_at($"stu_map", "students"))
    .withColumn("stu.values1", element_at($"`stu.values`", 1))
    .withColumn("stu.values1.name", element_at($"`stu.values1`", "name"))
    .withColumn("stu.values1.age", element_at($"`stu.values1`", "age"))
  df2_from.printSchema()
  df2_from.show(false)

  // 第2种方式，使用 StructType 指定 schema



  spark.stop()

}
