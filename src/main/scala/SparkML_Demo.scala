
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StructType, StructField, DoubleType, StringType}
//import scala.collection.immutable.Vector

// ml包是面向是spark中的DataSet的，而mllib是面向RDD的
//import org.apache.spark.ml._
//import org.apache.spark.mllib._
import org.apache.spark.ml.feature.{StringIndexer,VectorAssembler}
import org.apache.spark.ml.linalg.Vector


// XGBoost库
import ml.dmlc.xgboost4j.scala.spark.XGBoostClassifier


object SparkML_Demo {

  def main(args: Array[String]): Unit = {

    var flag = "local"
    val conf = new SparkConf().setAppName("sparkML_Demo")
    if (flag == "local"){conf.setMaster("local[*]")}
    val spark = SparkSession.builder().config(conf).getOrCreate()

    val data_path = ".\\input\\iris_data\\iris.data"

    val schema = new StructType(Array(
      StructField("sepal length", DoubleType, true),
      StructField("sepal width", DoubleType, true),
      StructField("petal length", DoubleType, true),
      StructField("petal width", DoubleType, true),
      StructField("class", StringType, true)))
    val iris_data = spark.read.schema(schema).csv(data_path)
//    val iris_data = spark.read.option("header","false").csv(data_path)
    iris_data.printSchema()
    iris_data.show()

//    处理class列
    val stringIndexer = new StringIndexer().
      setInputCol("class").
      setOutputCol("classIndex").
      fit(iris_data)
    val labelTransformed = stringIndexer.transform(iris_data).drop("class")
    labelTransformed.printSchema()
    labelTransformed.show()

//    处理特征
    val vectorAssembler = new VectorAssembler().
      setInputCols(Array("sepal length", "sepal width", "petal length", "petal width")).
      setOutputCol("features")
    val xgbInput = vectorAssembler.transform(labelTransformed).select("features", "classIndex")
    xgbInput.printSchema()
    xgbInput.show()

//    训练XGB模型
    val xgbParam = Map("eta" -> 0.1f,
      "max_depth" -> 2,
      "objective" -> "multi:softprob",
      "num_class" -> 3,
      "num_round" -> 100
//      "num_workers" -> 2
    )
    val xgbClassifier = new XGBoostClassifier(xgbParam).
      setFeaturesCol("features").
      setLabelCol("classIndex")
    val xgbClassificationModel = xgbClassifier.fit(xgbInput)


//    预测单个样本
    val features = xgbInput.head().getAs[Vector]("features")
    val result = xgbClassificationModel.predict(features)
    println(features)
    println(result)

    spark.stop()

  }

}
