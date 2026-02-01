package SparkDemos

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

/**
 * Spark 分位数分箱
 */
object SparkQuantileBinning {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(this.getClass.getSimpleName.stripSuffix("$"))
      .master("local[2]")
      .getOrCreate()
    import spark.implicits._

    // 测试数据（包含 null、正数、负数）
    val df = Seq(
      Some(-10.5),
      Some(-2.3),
      Some(0.0),
      Some(1.1),
      Some(3.7),
      Some(5.0),
      Some(8.9),
      Some(12.4),
      Some(20.0),
      None,
      Some(-100.0),
      Some(100.0)
    ).toDF("value")
    // df.show()

    val result = assignQuantileScore(df, "value", "quantile_score")
    result.show(false)

    spark.stop()
  }

  private def assignQuantileScore(df: DataFrame, valueColName: String, resColName: String): DataFrame = {
    // Step 1: 计算十分位数（10% ～ 90%，共9个分位点）
    val quantiles = df.stat.approxQuantile(valueColName, Array(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9), 0.001)
    // quantiles.length == 9
    val valueCol: Column = col(valueColName)
    // Step 2: 构建 quantile 映射表达式
    var quantileExpr: Column = when(valueCol.isNull, lit(null)) // 保留 null，也可改为 lit(0)
    // 区间 0: (-∞, q0] → 0
    quantileExpr = quantileExpr.when(valueCol <= quantiles(0), lit(0))
    // 区间 1～8: (q_{i-1}, q_i] → i
    for (i <- 1 until 9) {
      quantileExpr = quantileExpr.when(
        valueCol > quantiles(i - 1) && valueCol <= quantiles(i),
        lit(i)
      )
    }
    // 区间 9: (q8, +∞) → 9
    quantileExpr = quantileExpr.when(valueCol > quantiles(8), lit(9))
    // 兜底（理论上不会触发）
    quantileExpr = quantileExpr.otherwise(lit(null))
    // Step 3: 添加新列
    df.withColumn(resColName, quantileExpr)
  }

}
