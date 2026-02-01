package SparkDemos

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

/**
 * 展示将 单例对象 的方法注册为 Spark UDF 时，配合广播变量的使用。
 */
object SparkUDFWithBroadCastValueUsage {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setAppName(this.getClass.getSimpleName.stripSuffix("$"))
      .setMaster("local[4]")
    val spark = SparkSession.builder()
      .config(sparkConf)
      .getOrCreate()
    import spark.implicits._

    val someDF = Seq("K1", "K2", "K3", "K4", "K5").toDF("key")
    println("=== Some Data ===")
    someDF.show()

    // 将单例对象的私有属性封装为广播变量
    val broadcastMap: Broadcast[Map[String, Int]] = spark.sparkContext.broadcast(SomeMap)

    println("=== UDF With Broadcast Map Usage ===")
    // 方式1：someMapFuncV1 里引用了单例对象私有属性 —— 错误
    val someUDFV1 = udf(someMapFuncV1 _)
    // 方式2：someMapFuncV2 使用广播变量作为参数 —— 错误
    val someUDFV2 = udf(someMapFuncV2 _)
    // 下面调用时，IDEA 会提示参数 broadcastMap "Type mismatch", required type 是 Column
    // someDF.withColumn("value", someUDFV1($"key", broadcastMap)).show()
    // 方式3：someMapFuncV3 使用柯里化+偏函数来使用广播变量 —— 错误
    // val someUDFV3 = udf((key: String) => someMapFuncV3(key)(broadcastMap))
    // 方式4：someMapFuncV4 使用纯函数实现，但是在注册时，使用匿名函数的闭包来捕获广播变量 —— 正确
    val someUDFV4 = udf(
      // 这个匿名函数的定义里捕获了 broadcastMap，然后使用 .value 求值，传递给纯函数执行计算
      (key: String) => someMapFuncV4(key, broadcastMap.value)
    )

    someDF.withColumn("value", someUDFV4($"key")).show()

    spark.stop()
  }

  // 单例对象的私有属性，会在UDF里被使用
  private val SomeMap: Map[String, Int] = Map("K1" -> 1, "K2" -> 2, "K3" -> 3, "K4" -> 4, "K5" -> 5)

  /**
   * UDF实现方式 1: **错误实现**
   * 此方法中引用了单例对象的 SomeMap 属性，会导致 Task not serializable 异常。
   * Spark 需要将整个闭包（包括引用的 topInvestorSet）序列化并发送到 Executor，但是：
   *  1. object 本身不是 Serializable 的（除非显式继承）；
   *  2. 即使能序列化，Executor 上反序列化后会创建一个新的 SomeUDF 实例，其 topInvestorSet 是重新初始化的（虽然内容相同，但过程低效且可能失败）；
   *  3. 更重要的是：Scala 的 object 在反序列化时行为不确定，官方不推荐依赖其序列化。
   *
   * 解决办法有两个：
   * 1. 将 SomeMap 定义为方法的局部常量，适用于较小的集合；
   * 2. 将 SomeMap 封装为广播变量，适用于较大的集合。
   * 但是封装为广播变量时，有几个坑需要注意，见下面的函数实现。
   *
   * @param key
   * @return
   */
  def someMapFuncV1(key: String): Int = {
    SomeMap.getOrElse(key, 0)
  }

  /**
   * UDF实现方式 2: 将广播变量作为参数 —— **错误实现**
   * Spark UDF 不支持将 Broadcast 变量作为参数传入，UDF的参数必须是 `Column` 对象 或字面量常量
   *
   * @param key
   * @param broadcastMap
   * @return
   */
  def someMapFuncV2(key: String, broadcastMap: Broadcast[Map[String, Int]]): Int = {
    broadcastMap.value.getOrElse(key, 0)
  }

  /**
   * UDF实现方式 3: 使用柯里化的偏函数封装广播变量 —— **错误实现**
   * 关键问题在于：Broadcast 对象不能被反序列化后直接使用：
   *  - Broadcast（如 TorrentBroadcast）是一个 运行时上下文相关的对象；
   *  - 它内部持有对 SparkContext、BlockManager 等的引用；
   *  - 当把它作为闭包的一部分序列化到 Executor 时，反序列化后的 Broadcast 对象是“残缺”的 —— 它失去了与 Driver 的连接；
   *  - 调用 .value 时会抛出异常
   * 因此通过偏函数闭包捕获的 broadcastMap 参数，在方法内部是无法使用的
   * @param key
   * @param broadcastMap
   * @return
   */
  def someMapFuncV3(key: String)(broadcastMap: Broadcast[Map[String, Int]]): Int = {
    val map = broadcastMap.value
    map.getOrElse(key, 0)
  }

  /**
   * UDF实现方式 4: 正确实现。
   * 方法应当是纯函数，只依赖于输入的参数，并且参数是Scala基本类型和集合。
   * 此时的重点是注册 UDF 时如何传参。
   * 这里使用匿名函数来捕获广播变量。
   * @param key
   * @param map
   * @return
   */
  def someMapFuncV4(key: String, map: Map[String, Int]): Int = {
    map.getOrElse(key, 0)
  }

}
