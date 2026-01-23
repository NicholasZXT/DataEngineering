package SparkDemos

/**
 * SparkSQL 应用开发一般需要关注如下3个包：
 * - `org.apache.spark.sql` → 程序骨架 & 数据容器
 * - `org.apache.spark.sql.types` → 数据结构 & Schema 定义
 * - `org.apache.spark.sql.functions` → 数据变换 & 逻辑表达
 */

import org.apache.spark.{SparkConf, SparkContext}
// ------- sql -------
// 常用的是如下的对象/类
import org.apache.spark.sql.{
  SparkSession, DataFrame, Dataset, Column, ColumnName,
  DataFrameReader, DataFrameWriter, SaveMode
}
// 下面的类/对象和 SparkSession 提供的隐式转换有关，一般不需要显式导入
//import org.apache.spark.sql.{SQLImplicits, LowPrioritySQLImplicits, DatasetHolder}

// ------- sql.functions -------
// 下面这个 functions 是一个 Object，封装了许多SQL函数，包括UDF —— 经常使用
import org.apache.spark.sql.functions.{
  col, column, lit, expr, get_json_object
}

// ----- 下面是 org.apache.spark-catalyst 包里的内容 -----
// SparkSQL的类型申明，其中 DataType 是所有类型的基类 —— types 属于 spark-catalyst 依赖（不是 spark-sql 依赖）
import org.apache.spark.sql.types.{
  StructType, StructField, DataType,
  StringType, IntegerType, LongType, FloatType, DecimalType, DoubleType, BooleanType, BinaryType,
  MapType, ArrayType
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
    // import spark.implicits._

    schemaUsage(spark)
    expressionUsage(spark)
    naUsage(spark)
    statUsage(spark)
    // otherUsage(spark)

    spark.stop()

  }

  /**
   * 展示 SparkSQL 类型系统的 schema 定义
   * @param spark
   */
  def schemaUsage(spark: SparkSession): Unit = {
    println("--------- SparkSQL schemaUsage ---------")
    // 使用 StructType 的 apply 方法
    val schema = StructType(
      // 也可以使用List
      Seq(
        StructField("id", IntegerType, nullable = false).withComment("用户ID"),
        StructField("name", StringType, nullable = true).withComment("姓名"),
        StructField("age", IntegerType, nullable = true).withComment("年龄"),
      )
    )
    // 也可以使用 add() 方法添加 Field
    schema.add(StructField("tags", ArrayType(StringType), nullable = true).withComment("标签"))
    schema.add(StructField("metadata", MapType(StringType, StringType), nullable = true).withComment("其他信息"))

    // ------ StructType 有如下有用的方法：------
    val fields: Array[StructField] = schema.fields
    val fields_name: Array[String] = schema.fieldNames
    // 根据名称/index获取某个列的类型
    val col1: StructField = schema("name")
    val col2: StructField = schema(2)
    // 各类打印方法
    println("schema.toDDL:")
    println(schema.toDDL)
    println("schema.sql:")
    println(schema.sql)
    println("schema.catalogString:")
    println(schema.catalogString)
    println("schema.simpleString:")
    println(schema.simpleString)
    println("schema.treeString:")
    schema.printTreeString()  // 等价于 println(schema.treeString)

    // ------ 手动定义 DataFrame 的 schema ------
    println("手动定义 DataFrame 的 schema:")
    val rows = Seq(
      Row(1, "Alice", 24, Seq("dev", "spark"), Map("dept" -> "eng")),
      Row(2, "Bob", 25, Seq("ops"), Map("dept" -> "infra"))
    )
    val df = spark.createDataFrame(spark.sparkContext.parallelize(rows), schema)
    df.printSchema()
    df.show()
  }

  /**
   * 展示 SparkSQL 表达式使用
   * @param spark
   */
  def expressionUsage(spark: SparkSession): Unit = {
    println("--------- SparkSQL expressionUsage ---------")

  }


  /**
   * SparkSQL DataFrameNaFunctions 缺失值处理
   * @param spark
   */
  def naUsage(spark: SparkSession): Unit = {
    println("--------- SparkSQL naUsage ---------")
    // 构建包含缺失值的测试数据集
    val schema = new StructType()
      .add("id", IntegerType)
      .add("name", StringType)
      .add("age", IntegerType)
      .add("score", DoubleType)
      .add("address", StringType)
    val data = Seq(
      Row(1, "Alice", 25, 85.5, null),       // address为空
      Row(2, "Bob", null, 90.0, "Beijing"),  // age为空
      Row(3, null, 28, Double.NaN, "Shanghai"), // name为空，score为NaN
      Row(4, "Charlie", 30, null, ""),       // score为空，address为空字符串
      Row(5, "David", 22, 78.0, "Guangzhou") // 无缺失值
    )
    val dataRDD = spark.sparkContext.parallelize(data)
    val df = spark.createDataFrame(dataRDD, schema).toDF(schema.fieldNames: _*)
    println("原始数据集：")
    df.show()
    // 输出：
    // +---+-------+----+-----+---------+
    // | id|   name| age|score|  address|
    // +---+-------+----+-----+---------+
    // |  1|  Alice|  25| 85.5|     null|
    // |  2|    Bob|null| 90.0|  Beijing|
    // |  3|   null|  28|  NaN| Shanghai|
    // |  4|Charlie|  30| null|         |
    // |  5|  David|  22| 78.0|Guangzhou|
    // +---+-------+----+-----+---------+

    // ========== 1. drop()：删除包含缺失值的行 ==========
    // 1.1 删除所有包含任意缺失值的行（默认行为）
    val dfDropAny = df.na.drop()
    println("\n1.1 删除所有含缺失值的行：")
    dfDropAny.show()
    // 输出：仅保留第5行无缺失值的数据
    // +---+-----+---+-----+---------+
    // | id| name|age|score|  address|
    // +---+-----+---+-----+---------+
    // |  5|David| 22| 78.0|Guangzhou|
    // +---+-----+---+-----+---------+

    // 1.2 仅删除指定列中存在缺失值的行
    val dfDropSpecified = df.na.drop(Array("age", "score")) // 仅age/score为空时删除
    println("\n1.2 仅删除age/score含缺失值的行：")
    dfDropSpecified.show()
    // 输出：第2行（age空）、第3行（score NaN）、第4行（score空）被删除
    // +---+-----+---+-----+---------+
    // | id| name|age|score|  address|
    // +---+-----+---+-----+---------+
    // |  1|Alice| 25| 85.5|     null|
    // |  5|David| 22| 78.0|Guangzhou|
    // +---+-----+---+-----+---------+

    // 1.3 仅删除缺失值数量达到阈值的行（thresh：非空值数量≥阈值才保留）
    val dfDropThresh = df.na.drop(minNonNulls = 4) // 非空值≥4个才保留
    println("\n1.3 保留非空值≥4个的行：")
    dfDropThresh.show()
    // 输出：第3行（name空+score NaN，非空值=3）被删除，其余保留
    // +---+-------+----+-----+---------+
    // | id|   name| age|score|  address|
    // +---+-------+----+-----+---------+
    // |  1|  Alice|  25| 85.5|     null|
    // |  2|    Bob|null| 90.0|  Beijing|
    // |  4|Charlie|  30| null|         |
    // |  5|  David|  22| 78.0|Guangzhou|
    // +---+-------+----+-----+---------+

    // ========== 2. fill()：填充缺失值 ==========
    // 2.1 对所有同类型列填充相同值
    val dfFillAll = df.na.fill(Map(
      "age" -> 0,        // 整数列age填充0
      "score" -> 0.0,    // 浮点列score填充0.0
      "name" -> "未知",   // 字符串列name填充"未知"
      "address" -> "无地址" // 字符串列address填充"无地址"
    ))
    println("\n2.1 按列类型填充缺失值：")
    dfFillAll.show()
    // 输出：所有NULL/NaN都被对应值填充，空字符串不会被默认填充
    // +---+-------+---+-----+---------+
    // | id|   name|age|score|  address|
    // +---+-------+---+-----+---------+
    // |  1|  Alice| 25| 85.5|    无地址|
    // |  2|    Bob|  0| 90.0|  Beijing|
    // |  3|    未知| 28|  0.0| Shanghai|
    // |  4|Charlie| 30|  0.0|         |
    // |  5|  David| 22| 78.0|Guangzhou|
    // +---+-------+---+-----+---------+

    // 2.2 仅对指定列填充值（处理空字符串）
    val dfFillSpecified = df.na.fill("无地址", Array("address")) // 仅address列填充
      .na.replace("address", Map("" -> "无地址")) // 把空字符串替换为"无地址"
    println("\n2.2 仅填充address列+替换空字符串：")
    dfFillSpecified.show()
    // 输出：
    // +---+-------+----+-----+---------+
    // | id|   name| age|score|  address|
    // +---+-------+----+-----+---------+
    // |  1|  Alice|  25| 85.5|    无地址|
    // |  2|    Bob|null| 90.0|  Beijing|
    // |  3|   null|  28|  NaN| Shanghai|
    // |  4|Charlie|  30| null|    无地址|
    // |  5|  David| 22| 78.0|Guangzhou|
    // +---+-------+----+-----+---------+

    // ========== 3. replace()：替换指定值（含缺失值） ==========
    // 替换数值列的NaN/NULL为指定值，或替换特定值
    val dfReplace = df.na.replace("score", Map(Double.NaN -> 0.0)) // NaN替换为0.0
      .na.fill(0.0, Array("score")) // 剩余NULL填充为0.0
    println("\n3. 替换score列的NaN/NULL为0.0：")
    dfReplace.show()
    // 输出：
    // +---+-------+----+-----+---------+
    // | id|   name| age|score|  address|
    // +---+-------+----+-----+---------+
    // |  1|  Alice|  25| 85.5|     null|
    // |  2|    Bob|null| 90.0|  Beijing|
    // |  3|   null|  28|  0.0| Shanghai|
    // |  4|Charlie|  30|  0.0|         |
    // |  5|  David| 22| 78.0|Guangzhou|
    // +---+-------+----+-----+---------+
  }

  /**
   * 展示 SparkSQL DataFrameStatFunctions 用法
   * @param spark
   */
  def statUsage(spark: SparkSession): Unit = {
    println("--------- SparkSQL statUsage ---------")
    import spark.implicits._
    // 构建测试数据集（学生成绩+消费数据）
    val data = Seq(
      (1, "Alice", 85.5, 90.0, 200.0, "math"),
      (2, "Bob", 78.0, 85.5, 150.0, "english"),
      (3, "Charlie", 92.0, 88.0, 300.0, "math"),
      (4, "David", 88.5, 91.0, 250.0, "math"),
      (5, "Ella", 75.0, 80.0, 180.0, "english"),
      (6, "Frank", 90.0, 89.5, 220.0, "math"),
      (7, "Grace", 82.0, 86.0, 190.0, "english")
    )

    val df = data.toDF("id", "name", "score1", "score2", "consume", "subject")
    println("原始数据集：")
    df.show()
    // 输出：
    // +---+-------+------+------+-------+-------+
    // | id|   name|score1|score2|consume|subject|
    // +---+-------+------+------+-------+-------+
    // |  1|  Alice|  85.5|  90.0|  200.0|   math|
    // |  2|    Bob|  78.0|  85.5|  150.0|english|
    // |  3|Charlie|  92.0|  88.0|  300.0|   math|
    // |  4|  David|  88.5|  91.0|  250.0|   math|
    // |  5|   Ella|  75.0|  80.0|  180.0|english|
    // |  6|  Frank|  90.0|  89.5|  220.0|   math|
    // |  7|  Grace|  82.0|  86.0|  190.0|english|
    // +---+-------+------+------+-------+-------+

    // ========== 1. corr()：相关性分析 ==========
    // 计算两列的皮尔逊相关系数（-1~1，越接近1正相关越强）
    val cov = df.stat.cov("score1", "score2")
    val corrScore = df.stat.corr("score1", "score2") // score1与score2的相关性
    val corrConsume = df.stat.corr("score1", "consume") // score1与消费的相关性
    println(s"\n2. 相关性分析：")
    println(s"score1与score2的协方差：$cov") // 输出≈0.89（强正相关）
    println(s"score1与score2的相关系数：$corrScore") // 输出≈0.89（强正相关）
    println(s"score1与consume的相关系数：$corrConsume") // 输出≈0.91（强正相关）

    // ========== 2. approxQuantile()：近似分位数计算 ==========
    // 参数：列名、分位数数组、误差（0=精确计算，适合小数据；0.01=近似，适合大数据）
    val quantiles = df.stat.approxQuantile(
      "consume",          // 目标列
      Array(0.25, 0.5, 0.75), // 要计算的分位数（25%、50%、75%）
      0.0                  // 误差（0=精确）
    )
    println(s"\n3. consume列的分位数：")
    println(s"25%分位数：${quantiles(0)}") // 输出185.0
    println(s"50%分位数（中位数）：${quantiles(1)}") // 输出200.0
    println(s"75%分位数：${quantiles(2)}") // 输出235.0

    // ========== 3. freqItems()：频繁项挖掘 ==========
    // 挖掘指定列中出现频率≥阈值的项（适合类别型列）
    val freqItemsDF = df.stat.freqItems(
      Array("subject", "score1"), // 要分析的列
      0.4                        // 最小支持度（出现频率≥40%）
    )
    println("\n4. 频繁项挖掘结果：")
    freqItemsDF.show(false)
    // 输出：math出现4次（占比≈57%）、85.5出现1次（占比≈14%，但因误差/阈值显示）
    // +----------------+---------------+
    // |subject_freqItems|score1_freqItems|
    // +----------------+---------------+
    // |[math, english] |[85.5]         |
    // +----------------+---------------+

    // ========== 4. crosstab()：交叉表（列联表） ==========
    // 分析两个类别列的分布关系（类似SQL的GROUP BY + COUNT）
    val crossTabDF = df.stat.crosstab("subject", "score1")
    println("\n5. subject与score1的交叉表：")
    crossTabDF.show(false)
    // 输出：展示每个科目下各分数的出现次数
    // +----------------+-----+-----+-----+------+------+------+------+
    // |subject_score1  |75.0 |78.0 |82.0 |85.5  |88.5  |90.0  |92.0  |
    // +----------------+-----+-----+-----+------+------+------+------+
    // |english         |1    |1    |1    |0     |0     |0     |0     |
    // |math            |0    |0    |0    |1     |1     |1     |1     |
    // +----------------+-----+-----+-----+------+------+------+------+

    // ========== 5. sampleBy()：按列分层抽样 ==========
    // 按subject列分层抽样，math抽30%，english抽50%
    val sampleDF = df.stat.sampleBy(
      "subject",                        // 分层列
      Map("math" -> 0.3, "english" -> 0.5), // 各层抽样比例
      12345                             // 随机种子（保证结果可复现）
    )
    println("\n6. 分层抽样结果（每次运行可能略有不同）：")
    sampleDF.show()
  }

  def otherUsage(spark: SparkSession): Unit = {
    println("--------- SparkSQL otherUsage ---------")
    val data = Seq(
      (1, 18, "male", 90, "A"),
      (2, 24, "female", 80, "B"),
      (3, 28, "male", 50, "D"),
    )
    val df = spark.createDataFrame(data).toDF("id", "age", "gender", "score", "grade")
    df.printSchema()
    //df.rdd.foreach(row => println(row.getClass.getName))
    // org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema

    val row = Row(1, 18, "male", 90, "A")
    println(row.getClass.getName)  // org.apache.spark.sql.catalyst.expressions.GenericRow
    println(row)
  }

}
