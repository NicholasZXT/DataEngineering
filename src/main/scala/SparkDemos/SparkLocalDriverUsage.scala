package SparkDemos

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

/**
 * 展示如何在本地IDEA里调试Spark程序（本地Spark Driver），并连接到远程的YARN 集群。
 * **不推荐生产环境这么用**。
 */
object SparkLocalDriverUsage {

  /**
   * 本地IDEA调试时，需要设置两个环境变量：
   *  - HADOOP_USER_NAME: 指定集群用户名，否则会使用本机当前用户，可能没有权限访问 HDFS/YARN 资源
   *  - HADOOP_CONF_DIR: 指定Hadoop的配置文件路径
   */
  def main(args: Array[String]): Unit = {
    println("=== SparkLocalDriverUsage ===")

    val sparkConf = new SparkConf()
    val hadoopConf = readAndCheckHadoopConf()
    // 需要手动加载 Hadoop/Hive 相关配置文件，并合并到 SparkConf 中
    mergeHadoopConf(sparkConf, hadoopConf)

    // 本地IDEA向YARN提交Spark任务时，需要手动配置 spark.yarn.jars，该配置项用于指定 Spark运行环境jar包所在路径，有两种方式：
    // 1. 将对应版本的Spark发现包下的jars目录下的包全部都上传到 HDFS 上，然后指定 hdfs 路径，这样只需要上传一次，本地调试时不需要再上传
    val sparkJars = "hdfs:///user/someone/spark-2.4.8-jars/*.jar"
    //  2. 添加本地的Spark相关jar包路径，这样每次都要触发上传 —— 不推荐
    val m2Repo = "file:///D:BigDataEnv/maven-repository"
    val sparkVersion = "2.4.8"
    val scalaVer = "2.11"
    // val sparkJars = s" ${m2Repo}/org/apache/spark/spark-*_${scalaVer}/${sparkVersion}/*.jar"
    println(s"sparkJars: $sparkJars")

    val spark = SparkSession.builder()
      .config(sparkConf)
      .appName(SparkLocalDriverUsage.getClass.getSimpleName)
      .config("spark.executor.instances", "1")
      .config("spark.executor.memory", "4g")
      // 设置 master 为 yarn-client 模式，并指定队列
      .master("yarn")
      .config("spark.yarn.queue", "spark")
      // 指定HDFS上的Spark运行环境jar包所在路径，该路径里需要有如下几类jar包：
      //  1. Spark运行环境jar包，建议找对应版本的Spark分发包（without-hadoop版），将其jars目录下的所有jar包都添加到spark.yarn.jars中
      //  2. 集群对应Hadoop版本的 hadoop-yarn-*.jar 和 hadoop-mapreduce-client-*.jar
      .config("spark.yarn.jars", sparkJars)
      // 要显式设置Driver的IP地址，否则会默认使用本机的主机名
      .config("spark.driver.host", "10.3.4.28")
      .config("spark.driver.bindAddress", "0.0.0.0")
      // 如果要开启 Spark-WebUI，则需要在 driver 端引入 org.apache.hadoop:hadoop-yarn-server-web-proxy 依赖
      //.config("spark.ui.enabled", "false")
      // 如果加载的HDFS配置 core-site.xml 中配置了topology的脚本（用于机架感知），一定要注释掉
      //.config("spark.hadoop.net.topology.script.file.name", "")
      //.config("spark.hadoop.net.topology.node.switch.mapping.impl", "org.apache.hadoop.net.ScriptBasedMapping")
      //.config("spark.hadoop.net.topology.node.switch.mapping.impl", "org.apache.hadoop.net.DefaultDNSToSwitchMapping")
      // 开启 Hive Catalog，需要确保classpath中有Hive的catalog依赖
      .enableHiveSupport()
      .getOrCreate()

    println("=== Listing Databases ===")
    spark.catalog.listDatabases().show(truncate = false)

    println("=== Listing Tables ===")
    // 需要确保该数据库中没有其他类型的table，比如hive-hbase映射表，如果有的话，还需要在classpath中添加HBase的相关依赖，比较麻烦
    spark.catalog.listTables("test_db").show(truncate = false)
    //spark.sql("USE test_db")
    //spark.catalog.listTables().show()

    println("=== Querying Some Table ===")
    val test_table = spark.sql(
      """
        |SELECT *
        |FROM test_db.some_user
        |LIMIT 100
        |""".stripMargin)
    test_table.show()

    spark.stop()
  }

  /**
   * 本地运行Spark程序时，需要手动读取 Hadoop/Hive 配置。
   * 此函数会尝试读取两个环境变量：
   *   - HADOOP_USER_NAME: 集群用户名，否则会使用本机当前用户，可能没有权限访问 HDFS/YARN 资源。
   *   - HADOOP_CONF_DIR: Hadoop配置文件路径，里面需要包含 core-site.xml, yarn-site.xml, hdfs-site.xml 等配置文件；
   *                      如果Spark还启用了Hive，则还需要 hive-site.xml。
   * @return
   */
  private def readAndCheckHadoopConf(): Configuration = {
    // 检查Hadoop环境变量及其配置
    val hadoop_user = Option(System.getenv("HADOOP_USER_NAME"))
    println(s"HADOOP_USER_NAME: $hadoop_user")
    val hadoop_conf_env = Option(System.getenv("HADOOP_CONF_DIR"))
    println(s"HADOOP_CONF_DIR: $hadoop_conf_env")
    if (hadoop_conf_env.isEmpty) {
      println("HADOOP_CONF_DIR is not set.")
      System.exit(1)
    }
    // 手动加载 Hadoop 配置文件
    val hadoopConf = new Configuration()
    hadoopConf.addResource(new Path(s"${hadoop_conf_env.get}/core-site.xml"))
    hadoopConf.addResource(new Path(s"${hadoop_conf_env.get}/yarn-site.xml"))
    hadoopConf.addResource(new Path(s"${hadoop_conf_env.get}/hdfs-site.xml"))
    // 如果要读取 Hive 表，也需要手动添加
    hadoopConf.addResource(new Path(s"${hadoop_conf_env.get}/hive-site.xml"))

    // 打印关键 YARN HA 配置
    println("=== Loaded YARN Configurations ===")
    println(s"yarn.resourcemanager.cluster-id =  ${hadoopConf.get("yarn.resourcemanager.cluster-id")}")
    println(s"yarn.resourcemanager.zk-address =  ${hadoopConf.get("yarn.resourcemanager.zk-address")}")
    println(s"yarn.resourcemanager.ha.enabled =  ${hadoopConf.get("yarn.resourcemanager.ha.enabled")}")
    println(s"yarn.resourcemanager.ha.rm-ids =  ${hadoopConf.get("yarn.resourcemanager.ha.rm-ids")}")
    println(s"yarn.resourcemanager.address.some-yarn-node-id =  ${hadoopConf.get("yarn.resourcemanager.address.some-yarn-node-id")}")
    println("==================================")

    hadoopConf
  }

  /**
   * 手动将加载的Hadoop配置，合并到SparkConf中。
   * 本地IDEA执行时，由于没有 spark-submit 脚本的帮助，hadoop的配置文件需要自己手动加载并合并到SparkConf中。
   * @param sparkConf
   * @param hadoopConf
   */
  private def mergeHadoopConf(sparkConf: SparkConf, hadoopConf: Configuration): Unit = {
    import scala.collection.JavaConverters._
    hadoopConf.iterator().asScala.foreach { entry =>
      sparkConf.set(s"spark.hadoop. ${entry.getKey}", entry.getValue)
    }
  }
}
