package SparkDemos

import java.nio.file.{Files, Path, Paths}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{
  Column, ColumnName, Row, DataFrame, DataFrameReader, DataFrameWriter, Dataset,
  SparkSession, SaveMode, Encoders
}
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}
import org.apache.spark.sql.functions.{element_at}
import org.apache.spark.sql.catalyst.encoders.RowEncoder
// SparkML 中特别需要注意的是下面两个类型
import org.apache.spark.mllib.linalg.{Vector, Vectors}
// 基本转换器
import org.apache.spark.ml.feature.{
  Imputer, ImputerModel, MinMaxScaler, MinMaxScalerModel, StandardScaler, StandardScalerModel,
  OneHotEncoder, OneHotEncoderEstimator, OneHotEncoderModel, StringIndexer, StringIndexerModel, IndexToString,
  VectorIndexer, VectorIndexerModel, VectorSlicer,
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
// 模型评估
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, RegressionEvaluator, MulticlassClassificationEvaluator}


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

    //------ SparkML数据输入规范 ------
    // SparkML里大部分的机器学习算法的输入都是一个如下两列的DataFrame： 标签列 - Double类型，特征列 - Vector[Double]类型
    val data = spark.createDataFrame(Seq(
      (1.0, Vectors.dense(0.0, 1.1, 0.1)),
      (0.0, Vectors.dense(2.0, 1.0, -1.0)),
      (0.0, Vectors.dense(2.0, 1.3, 1.0)),
      (1.0, Vectors.dense(0.0, 1.2, -0.5))
    )).toDF("label", "features")
    //data.printSchema()
    //data.show(numRows = 5, truncate = false)
    // 如果想从其中的 features 列的Vector获取值，那么需要使用如下的方式
    // [Spark Scala: How to convert Dataframe[vector] to DataFrame[f1:Double, ..., fn: Double)]](https://stackoverflow.com/questions/38110038/spark-scala-how-to-convert-dataframevector-to-dataframef1double-fn-d)
    val data_features = data.map{
      case Row(label: Double, features: Vector) => (label, features(0), features(1), features(2))
    }.toDF("label", "f1", "f2", "f3")
    //data_features.printSchema()
    //data_features.show(numRows = 5, truncate = false)


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
    //irisData.printSchema()
    //irisData.show(numRows = 5, truncate = false)

    //------ 回归问题：Boston房价数据集 ------
    val bostonFileName: Path = Paths.get("src", "main", "resources", "hadoop_data", "boston_housing", "HousingData.csv")
    val bostonDataPath: Path = currentDir.resolve(bostonFileName)
    val bostonData = spark.read.option("header", "true").csv(bostonDataPath.toString)
    //bostonData.printSchema()
    //bostonData.show(numRows = 5, truncate = false)

    //------ 特征工程mock数据 ------
    val mockData = Seq(
      (1, 18, "male", 90, "A"),
      (2, 24, "female", 80, "B"),
      (3, 28, "male", 50, "D"),
      (4, 30, "female", 70, "C"),
      (5, 34, "female", 75, "C"),
    )
    val df = spark.createDataFrame(mockData).toDF("id", "age", "gender", "score", "grade")
    //df.printSchema()
    //df.rdd.foreach(row => println(row.getClass.getName))

    // ----- 特征工程练习 --------
    numericFeatureTransform(spark, df)
    categoryFeatureTransform(spark, df)

    //------ 分类问题建模------
    irisDataClassification(spark, irisData)

    //------ 回归问题建模------
    bostonHousingRegression(spark, bostonData)

    spark.stop()
  }

  def numericFeatureTransform(spark: SparkSession, df: DataFrame): Unit = {
    println("======== numericFeatureTransform ========")
    import spark.implicits._
    // SparkML 里的 StandardScaler, MinMaxScaler, Normalizer 等不能直接作用于 Double 列，只能作用于 Vector 类型的列 ------ KEY
    // 可以使用 VectorAssembler 将多个 Double 列组合成一个 Vector 列
    val assembler = new VectorAssembler().setInputCols(Array("score")).setOutputCol("score_vec")
    val df_vec = assembler.transform(df)
    //df_vec.printSchema()
    //df_vec.show(numRows = 10, truncate = false)

    // 数值特征归一化
    val standardScaler = new StandardScaler()
      .setInputCol("score_vec")
      .setOutputCol("score_standard")
      .setWithStd(true)
      .setWithMean(true)
      .fit(df_vec)
    println("------ StandardScaler ------")
    standardScaler.transform(df_vec).show(numRows = 10, truncate = false)

    val minMaxScaler = new MinMaxScaler()
      .setInputCol("score_vec")
      .setOutputCol("score_minmax")
      //.setMax(5.0)
      //.setMin(1.0)
      .fit(df_vec)
    println("------ MinMaxScaler ------")
    minMaxScaler.transform(df_vec).show(numRows = 10, truncate = false)

    val normalizer = new Normalizer()
      .setInputCol("score_vec")
      .setOutputCol("score_norm")
      .setP(2.0)   // 这个不需要fit
    println("------ Normalizer ------")
    normalizer.transform(df_vec).show(numRows = 10, truncate = false)
  }

  def categoryFeatureTransform(spark: SparkSession, df: DataFrame): Unit = {
      println("======== categoryFeatureTransform ========")
    import spark.implicits._
    // 字符串转数值序号，注意，fit方法返回的是 StringIndexerModel
    val stringIndexer: StringIndexerModel = new StringIndexer()
      .setInputCol("gender")
      .setOutputCol("gender_label")
      .fit(df)
    val df_strIndexed = stringIndexer.transform(df)
    println("------ StringIndexer ------")
    df_strIndexed.show(numRows = 10, truncate = false)
    // 转回去
    val genderReverse = new IndexToString().setInputCol("gender_label").setOutputCol("gender_reversed")
    println("------ IndexToString ------")
    genderReverse.transform(df_strIndexed).show(numRows = 10, truncate = false)

    // OneHotEncoderEstimator（代替OneHotEncoder） 的输入必须是 numeric，不能直接处理 String，要先用 StringIndexer 转换
    val gradeStrIndexer: StringIndexerModel = new StringIndexer()
      .setInputCol("grade")
      .setOutputCol("grade_label")
      .fit(df)
    val df_gradeStrIndexed = gradeStrIndexer.transform(df)
    val oneHotEncoder: OneHotEncoderModel = new OneHotEncoderEstimator()
      .setInputCols(Array("grade_label"))
      .setOutputCols(Array("grade_onehot"))
      .setDropLast(false)
      .setHandleInvalid("error")
      .fit(df_gradeStrIndexed)
    println("------ OneHotEncoderEstimator ------")
    val df_onehot = oneHotEncoder.transform(df_gradeStrIndexed)
    df_onehot.printSchema()
    df_onehot.show(numRows = 10, truncate = false)
    // oneHot 返回的列的类型是 org.apache.spark.ml.linalg.SparseVector
  }

  def irisDataClassification(spark: SparkSession, irisData: DataFrame): Unit = {
    println("======== irisDataClassification ========")
    import spark.implicits._

    // 原始数据是 3 分类，这里只取两分类数据
    val irisDataFilter = irisData.filter($"class" === "Iris-setosa" || $"class" === "Iris-versicolor")

    // 处理class列
    val stringIndexer: StringIndexerModel = new StringIndexer()
      .setInputCol("class")
      .setOutputCol("class_label")
      .fit(irisDataFilter)
    val irisDataLabeled = stringIndexer.transform(irisDataFilter)
      //.drop("class")
    //irisDataLabeled.printSchema()
    //irisDataLabeled.show(numRows = 20, truncate = false)

    // 汇总所有特征列为 Vector
    val vectorAssembler = new VectorAssembler()
      .setInputCols(Array("sepal_length", "sepal_width", "petal_length", "petal_width"))
      .setOutputCol("features")
    val irisDataTrain = vectorAssembler.transform(irisDataLabeled)
    //irisDataTrain.show(numRows = 20, truncate = false)

    // 使用 LogisticRegression
    val lr = new LogisticRegression(uid = "irisData-LR")
      .setFeaturesCol("features")
      .setLabelCol("class_label")
      .setFitIntercept(true)     //  是否使用截距
      .setStandardization(true)  // 是否先对数据标准化
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)
    val lrModel: LogisticRegressionModel = lr.fit(irisDataTrain)
    println(s"LogisticRegression Model -> Coefficients: ${lrModel.coefficients}; Intercept: ${lrModel.intercept}")
    // 查看训练过程信息
    val trainingSummary = lrModel.binarySummary
    // Obtain the objective per iteration.
    val objectiveHistory = trainingSummary.objectiveHistory
    println("objectiveHistory:")
    objectiveHistory.foreach(loss => println(loss))
    // Obtain the receiver-operating characteristic as a dataframe and areaUnderROC.
    val roc = trainingSummary.roc
    roc.show()
    println(s"areaUnderROC: ${trainingSummary.areaUnderROC}")
    // 预测，使用 transform 方法
    val predictions = lrModel.transform(irisDataTrain)
    predictions.printSchema()
    predictions.show(numRows = 10, truncate = false)
    // 预测效果评估
    val evaluator = new BinaryClassificationEvaluator(uid = "irisData-LR-BinaryClassify-Evaluator")
      .setLabelCol("class_label")
      .setRawPredictionCol("prediction")
      .setMetricName("areaUnderROC")
    val aucScore = evaluator.evaluate(predictions)
    println(s"Test Error = ${aucScore}")
  }

  def bostonHousingRegression(spark: SparkSession, bostonData: DataFrame): Unit = {
    println("======== bostonHousingRegression ========")
  }

}
