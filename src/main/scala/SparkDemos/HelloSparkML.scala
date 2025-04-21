package SparkDemos

import java.nio.file.{Files, Path, Paths}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{Column, ColumnName, DataFrame, DataFrameReader, DataFrameWriter, Dataset, SaveMode, SparkSession}
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
//import org.apache.spark.mllib.rdd.MLPairRDDFunctions
// 基本转换器
import org.apache.spark.ml.feature.{
  Imputer, ImputerModel, MinMaxScaler, MinMaxScalerModel, StandardScaler, StandardScalerModel,
  OneHotEncoder, OneHotEncoderModel, StringIndexer, StringIndexerModel, VectorIndexer, VectorIndexerModel,
  Normalizer, Binarizer, Bucketizer, QuantileDiscretizer, PolynomialExpansion, PCA
}
// 高级转换器
import org.apache.spark.ml.feature.{RFormula, RFormulaModel, SQLTransformer, VectorAssembler}
// 分类模型
import org.apache.spark.ml.classification.{
  LogisticRegression, LogisticRegressionModel,
  DecisionTreeClassifier, DecisionTreeClassificationModel,
  RandomForestClassifier, RandomForestClassificationModel,
  GBTClassifier, GBTClassificationModel
}
// 回归模型
import org.apache.spark.ml.regression.{
  LinearRegression, LinearRegressionModel,
  DecisionTreeRegressor, DecisionTreeRegressionModel,
  RandomForestRegressor, RandomForestRegressionModel,
  GBTRegressor, GBTRegressionModel
}


/**
 * org.apache.spark: spark-mllib_${spark.scala.version} 包中有两类API:
 * - org.apache.spark.ml: DataFrame-based API，这个是Spark官方主推的API，并且一直在发展
 * - org.apache.spark.mllib: RDD-based APIs，从Spark 2.0 开始，就一直处于维护状态了，后续不会新增功能，但是也没有废弃的计划·
 * 这里主要介绍 DataFrame-based API。
 *
 * Spark-ML(DataFrame-based API) 是在SparkSQL的 DataFrame 上构建机器学习流程的，设计思路借鉴了 scikit-learn，有如下3个核心概念：
 * - Transformer：数据转换器
 * - Estimator：估计器
 * - Pipeline：管道，将多个Transformer和Estimator组合起来，实现机器学习流程
 *
 * Spark-ML(DataFrame-based API) 里的package常用内容组织如下：
 * - Transformer 抽象类: 所有Transformer的基类
 * - Estimator 抽象类: 所有Estimator的基类
 * - Model 抽象类: 表示 Fitted Model
 * - PipelineStage、Pipeline、PipelineModel: 管道，将多个Transformer和Estimator组合起来，实现机器学习流程
 * - Predictor抽象类、PredictionModel抽象类:
 * - org.apache.spark.ml.feature: 特征工程，比如特征提取、特征转换等
 * - org.apache.spark.ml.regression: 回归模型
 * - org.apache.spark.ml.classification: 分类模型
 * - org.apache.spark.ml.tree: 树模型的接口，主要的树模型实现放在了上面的 regression 和 classification 包里
 * - org.apache.spark.ml.clustering: 聚类模型
 * - org.apache.spark.ml.ann: artificial neural network (ANN)相关内容
 * - org.apache.spark.ml.evaluation: 损失函数
 * - org.apache.spark.ml.tuning: 交叉验证
 * - org.apache.spark.ml.param: 参数定义
 * - org.apache.spark.ml.stat: 统计量
 * - org.apache.spark.ml.optim:
 * - org.apache.spark.ml.linalg:
 */
object HelloSparkML {

  def main(args: Array[String]): Unit = {
    println(">>> Hello Spark Machine Learning")
    val conf = new SparkConf()
      .setAppName("Hello Spark Machine Learning")
      .set("spark.driver.memory", "1g")
      .set("spark.driver.cores", "1")
      .set("spark.executor.instances", "2")
      .set("spark.executor.cores", "2")
      .set("spark.executor.memory", "2g")
      .set("spark.default.parallelism", "2")
      .set("spark.ui.port", "8080")
      .setMaster("local")
    val spark = SparkSession.builder.config(conf).getOrCreate()
    import spark.implicits._

    val currentDir: Path = Paths.get(System.getProperty("user.dir"))
    println(s"currentDir '${currentDir}' exist: ${Files.exists(currentDir)}.")

    //------ 分类问题：Iris数据集 ------
    val irisFileName: Path = Paths.get("src", "main", "resources", "hadoop_data", "iris_data", "iris.data")
    val irisDataPath: Path = currentDir.resolve(irisFileName)
    val schema = new StructType(
      Array(
        StructField("sepal_length", DoubleType, nullable = true),
        StructField("sepal_width", DoubleType, nullable = true),
        StructField("petal_length", DoubleType, nullable = true),
        StructField("petal_width", DoubleType, nullable = true),
        StructField("class", StringType, nullable = true)
      )
    )
    val irisData = spark.read.schema(schema).csv(irisDataPath.toString)
    irisData.printSchema()
    irisData.show(numRows = 5, truncate = false)

    //------ 回归问题：Boston房价数据集 ------
    val bostonFileName: Path = Paths.get("src", "main", "resources", "hadoop_data", "boston_housing", "HousingData.csv")
    val bostonDataPath: Path = currentDir.resolve(bostonFileName)
    val bostonData = spark.read.option("header", "true").csv(bostonDataPath.toString)
    bostonData.printSchema()
    bostonData.show(numRows = 5, truncate = false)


    //------ 分类问题建模------
    irisDataClassification(irisData)

    //------ 回归问题建模------
    bostonHousingRegression(bostonData)

    spark.stop()

  }

  def irisDataClassification(irisData: DataFrame): Unit = {
    // 处理class列，注意，fit方法返回的是 StringIndexerModel
    val stringIndexer: StringIndexerModel = new StringIndexer().
      setInputCol("class").
      setOutputCol("classIndex").
      fit(irisData)
    //val labelTransformed = stringIndexer.transform(iris_data).drop("class")
    //labelTransformed.printSchema()
    //labelTransformed.show()

    // 处理特征
    //val vectorAssembler = new VectorAssembler().
    //  setInputCols(Array("sepal length", "sepal width", "petal length", "petal width")).
    //  setOutputCol("features")
    //val xgbInput = vectorAssembler.transform(labelTransformed).select("features", "classIndex")
    //xgbInput.printSchema()
    //xgbInput.show()
  }

  def bostonHousingRegression(bostonData: DataFrame): Unit = {
  }

}
