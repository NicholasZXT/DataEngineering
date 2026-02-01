package SparkDemos

import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{ArrayType, DataType, MapType, StringType, StructField, StructType}
import org.apache.spark.sql.functions.{col, element_at, explode, from_json, get_json_object, map_keys, map_values}


/**
 * SparkSQL 解析 JSON + explode实现行转列
 */
object SparkParsingJson extends App {

  val spark = SparkSession.builder()
    .appName("SparkSQL Json Parsing")
    .master("local[2]")
    .config("spark.local.dir", ".\\tmp")
    .getOrCreate()

  // 引入 spark 中的隐式转换，使用 $ 语法
  import spark.implicits._

  // 手动创建JSON结构的数据
  // 第 1 个：没有 JSON 数组
  val data1 = Seq(Row("stu1", "{\"student\": {\"name\": \"zhang\", \"age\": 24}}"))
  val schema1 = StructType(List(StructField("id", StringType), StructField("student", StringType)))
  // 第 2 个：有 JSON 数组
  val data2 = Seq(Row("grade1", "{\"students\": [{\"name\": \"zhang\", \"age\": 26}, {\"name\": \"zhou\", \"age\": 23}]}"))
  val schema2 = StructType(List(StructField("id", StringType), StructField("students", StringType)))
  val df1 = spark.createDataFrame(spark.sparkContext.makeRDD(data1), schema1)
  val df2 = spark.createDataFrame(spark.sparkContext.makeRDD(data2), schema2)
  df1.show(false)
  df2.show(false)

  /**
   * 使用 get_json_object:
   *  1. 第 2 个参数是要处理的 Column，包含了要解析的JSON字符串
   *  2. 第 2 个参数指定解析后的取值 path，$ 表示解析后的JSON对象. 访问对象使用 .，数组访问使用 []，注意，数组下标从 0 开始
   * 如果解析不成功，则返回 null
   */
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


  /**
   * 使用 from_json. 这个方法有多种签名，常见的是用如下两种，主要区别是 第 2 个参数的类型
   *  1. 第 1 个参数指定要解析的 Column
   *  2. 第 2 个参数常用的有两种类型： DataType 和 StructType，用于指定待解析 JSON 的 schema
   * 这个方法，会将JSON字符串解析成 SparkSQL 中的 复合数据类型，主要是 MapType 和 StructType —— 这两个都是 DataType 的子类。
   */
  // 第1种方式，使用 MapType 指定 schema，只需要指定 schema 里各个字段的数据类型，不需要指定字段名
  // MapType 类型的字段，需要使用 sql.functions 里面的函数来访问： 比如 map_keys(), map_values(), element_at()
  // 其中 element_at() 方法可以用来根据 key 访问 MapType 中的值，也可以根据 index 来访问 ArrayType 中的值
  val mapType_1 = MapType(StringType, MapType(StringType, StringType))
  val mapType_2 = MapType(StringType, ArrayType(MapType(StringType, StringType)))
  val df1_from_map = df1.withColumn("stu_map", from_json($"student", mapType_1))
    .withColumn("stu.value", element_at($"stu_map", "student"))   // 根据 key 访问 MapType 中的 值
    .withColumn("stu.value.name", element_at($"`stu.value`", "name"))  // 带 . 的列名需要用反引号
    .withColumn("stu.value.age", element_at($"`stu.value`", "age"))
  val df2_from_map = df2.withColumn("stu_map", from_json($"students", mapType_2))
    .withColumn("stu.values", element_at($"stu_map", "students"))
    .withColumn("stu.values[1]", element_at($"`stu.values`", 1))  // 根据 index 访问 ArrayType 中的值
    .withColumn("stu.values[1].name", element_at($"`stu.values[1]`", "name"))
    .withColumn("stu.values[1].age", element_at($"`stu.values[1]`", "age"))
  // 查看字段类型，可以发现是 stu_map 是 Map 类型
  // df1_from_map.printSchema()  // stu_map.value 也是 Map 类型
  // df1_from_map.show(false)
  // df2_from_map.printSchema()  // stu_map.values 是 array of map 类型
  // df2_from_map.show(false)

  // 第2种方式，使用 StructType 指定 schema，除了指定数据类型，还需要指定字段名
  // 对于 StructType 字段来说，访问其中的 field，可以直接使用 . 访问
  val structType_1 = new StructType()
    .add("student", StructType(List(StructField("name", StringType), StructField("age", StringType))))
  val structType_2 = new StructType()
    .add("students", ArrayType(StructType(Array(StructField("name", StringType), StructField("age", StringType)))))
  val df1_from_struct = df1.withColumn("stu_struct", from_json($"student", structType_1))
    .withColumn("stu_struct.student", $"stu_struct.student")
    .withColumn("stu_struct.student.name", $"stu_struct.student.name")
    .withColumn("stu_struct.student.age", $"stu_struct.student.age")
  val df2_from_struct = df2.withColumn("stu_struct", from_json($"students", structType_2))
    .withColumn("stu_struct.students", $"stu_struct.students")
    .withColumn("stu_struct.students[0]", element_at($"stu_struct.students", 1))
    .withColumn("stu_struct.students[0].name", $"`stu_struct.students[0]`.name")
    .withColumn("stu_struct.students[0].age", $"`stu_struct.students[0]`.age")
  // 可以看到, stu_map 是 struct 类型，而不是 map 类型
  // df1_from_struct.printSchema()  // stu_struct.students 也是 struct
  // df1_from_struct.show(false)
  // df2_from_struct.printSchema()  // stu_struct.students 是 array of struct
  // df2_from_struct.show(false)


  // -------------------------------------------------------------------------------------------------------------------

  /***
   * 使用 explode 完成行转列，需要配合 from_json 使用
   */
  // 将 df2 中 students 列的多个 student 展开成行, explode 要求被展开的 Column 的类型必须是 List 之类的，所以需要先提取出来
  // 使用 MapType 和 StructType 都可以
  val df2_explode_map = df2.withColumn("students_map", from_json($"students", mapType_2))
    .withColumn("student_list", element_at($"students_map", "students"))
    .withColumn("student", explode($"student_list"))
  val df2_explode_struct = df2.withColumn("students_struct", from_json($"students", structType_2))
    .withColumn("student_list", $"students_struct.students")
    .withColumn("student", explode($"student_list"))
  // df2_explode_map.printSchema()
  // df2_explode_map.show(false)
  // df2_explode_struct.printSchema()
  // df2_explode_struct.show(false)

  /**
   * explode 也可以使用 RDD + flatMap 来完成
   */
  val mapRdd = df2.withColumn("students_map", from_json($"students", mapType_2))
    .withColumn("student_list", element_at($"students_map", "students"))
    .rdd
    .flatMap(
      row => {
        // row 是一个 Row 对象(org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema)
        // println("row.getClass.getName: ", row.getClass.getName)
        val id = row.getString(0)
        val records = new ListBuffer[Row]()
        // 下面 getSeq 必须要指定泛型参数 [Map[String, String]]，否则后面 for 循环会报错，拿不到信息 ------ KEY
        val students = row.getSeq[Map[String, String]](row.fieldIndex("student_list"))
        // students 是一个 scala.collection.mutable.WrappedArray$ofRef
        // println("students.getClass.getName: ", students.getClass.getName)
        for (stu_map <- students){
          // 上面 getSeq 指定了泛型之后，这里才能拿到具体的类信息：scala.collection.immutable.Map$Map2
          // println("stu_map.getClass.getName: ", stu_map.getClass.getName)
          records.append(Row(id, stu_map.getOrElse("name", ""), stu_map.getOrElse("age", "")))
        }
        // students
        records
      }
    )
  // mapRdd.foreach(m => println(m.getClass.getName))
  // mapRdd.foreach(println)
  // 重新设置 schema
  val rdd_schema = new StructType()
    .add(StructField("id", StringType))
    .add(StructField("name", StringType))
    .add(StructField("age", StringType))
  // 从 RDD 创建 DataFrame
  val mapRdd_df = spark.createDataFrame(mapRdd, rdd_schema)
  mapRdd_df.printSchema()
  mapRdd_df.show()

  val structRdd = df2.withColumn("students_struct", from_json($"students", structType_2))
    .withColumn("student_list", $"students_struct.students")
    .rdd
    .flatMap(
      row => {
        val id = row.getString(0)
        val records = new ListBuffer[Row]()
        // student_list 是 array of StructType，而 StructType 这个可以再次使用 Row 来表示 --------- KEY
        // 这是因为 StructType 对应的就是 SparkSQL 的 Row 对象
        val students = row.getSeq[Row](row.fieldIndex("student_list"))
        for (stu_row <- students){
          records.append(Row(id, stu_row.getAs[String]("name"), stu_row.getAs[String]("age")))
        }
        // Array(1, 2)
        records
      }
    )
  // structRdd.printSchema()
  // structRdd.show(false)
  structRdd.foreach(println)
  val structRdd_df = spark.createDataFrame(structRdd, rdd_schema)
  structRdd_df.printSchema()
  structRdd_df.show(false)

  spark.stop()

}
