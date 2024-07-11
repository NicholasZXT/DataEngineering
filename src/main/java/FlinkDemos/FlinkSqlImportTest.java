package FlinkDemos;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/*
调试 FlinkSQL 读kafka 写Hive
----------------------------------------------------------
flink-sql 建表语句如下：
CREATE TABLE default_catalog.default_database.test_flink(
    `name` STRING,
    `content` STRING,
    `topic` string METADATA VIRTUAL,   -- from Kafka connector
    `partition` INT METADATA VIRTUAL,  -- from Kafka connector
    `offset` BIGINT METADATA VIRTUAL,  -- from Kafka connector
    `kafka_timestamp` TIMESTAMP(3) METADATA FROM 'timestamp'  -- kafka时间戳
)
WITH (
    'connector' = 'kafka',
    'topic' = 'test_flink',
    'properties.bootstrap.servers' = '10.8.6.185:9092',
    'scan.startup.mode' = 'earliest-offset',
    'format' = 'json',
    'json.fail-on-missing-field' = 'false',
    'json.ignore-parse-errors' = 'true'
)
----------------------------------------------------------
flink-sql 插入语句如下：
INSERT INTO test_db.test_flink
SELECT `name`, `content`, `topic`, `partition`, `offset`, `kafka_timestamp`
FROM default_catalog.default_database.test_flink
----------------------------------------------------------
hive建表语句如下：
CREATE TABLE test_db.test_flink(
    `name` STRING,
    `content` STRING,
    `topic` string,
    `partition` INT,
    `offset` BIGINT,
    `kafka_timestamp` TIMESTAMP COMMENT 'kafka时间戳'
)COMMENT 'flink测试表'
STORED AS TEXTFILE
TBLPROPERTIES (
    'sink.partition-commit.delay'='15 s',
    'sink.partition-commit.policy.kind'='metastore,success-file',
    'sink.partition-commit.trigger'='process-time'
);
----------------------------------------------------------
flink任务执行语句如下：
flink run -t yarn-per-job -Dyarn.application.name=kafka2hive-test_flink -c FlinkDemos.FlinkSqlImportTest DataEngineering-1.0.jar
----------------------------------------------------------
kafka控制台生产者语句如下：
kafka-console-producer.sh --broker-list 10.8.6.185:9092 --topic test_flink
{"name": "n1","content":"c1"}
{"name": "n2","content":"c2"}
{"name": "n3","content":"c3"}
{"name": "n4","content":"c4"}
{"name": "n5","content":"c5"}
{"name": "n6","content":"c6"}
{"name": "n7","content":"c7"}
{"name": "n8","content":"c8"}
{"name": "n9","content":"c9"}
{"name": "n10","content":"c10"}
 */
public class FlinkSqlImportTest {
    public static void main(String[] args) throws Exception {
        Path HIVE_CONF_DIR = Paths.get("/opt/hive-3.1.2-bin/conf/");
        String source_sql = "CREATE TABLE default_catalog.default_database.test_flink(\n" +
                "`name` STRING, `content` STRING, `topic` string METADATA VIRTUAL, `partition` INT METADATA VIRTUAL, " +
                "`offset` BIGINT METADATA VIRTUAL, `kafka_timestamp` TIMESTAMP(3) METADATA FROM 'timestamp')\n" +
                "WITH ('connector' = 'kafka', 'topic' = 'test_flink', 'properties.bootstrap.servers' = '10.8.6.185:9092'," +
                "'scan.startup.mode' = 'earliest-offset', 'format' = 'json', 'json.fail-on-missing-field' = 'false',"+
                "'json.ignore-parse-errors' = 'true')";
        String sink_sql = "INSERT INTO test_db.test_flink\n" +
                "SELECT `name`, `content`, `topic`, `partition`, `offset`, `kafka_timestamp`\n" +
                "FROM default_catalog.default_database.test_flink";

        // 初始化flink环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        // 每隔1分钟保存一次检查点
        env.enableCheckpointing(1000*60);
        env.disableOperatorChaining();

        System.out.println("getCurrentCatalog: " + tEnv.getCurrentCatalog());
        System.out.println("getCurrentDatabase: " + tEnv.getCurrentDatabase());
        // 建立 kafka topic对应 动态表——这个表是建立在 default_catalog.default_database 命名空间下的
        tEnv.executeSql(source_sql);

        // 本地调试输出代码
        //Table table = tEnv.sqlQuery("select company_name, company_id, regexp_replace(judgment, '\\r|\\n|\\t', '') AS judgment_clean from tyc_lawsuit_notice limit 20");
        //DataStream<Row> stream = tEnv.toDataStream(table);
        //stream.print();
        //env.execute();
        // 另一种调试输出方式
        //tEnv.executeSql("CREATE TABLE console_table\n" +
        //        "(company_name string, company_id string, topic string, `partition` int, `offset` bigint, judgment string)\n" +
        //        "WITH ('connector'='print')"
        //);
        //tEnv.executeSql("INSERT INTO console_table\n" +
        //        "SELECT company_name, company_id, regexp_replace(judgment, '\\r|\\n|\\t', '') AS judgment \n" +
        //        "FROM tyc_lawsuit_notice LIMIT 100"
        //);

        // 读取Hive metastore元数据，建立hive的catalog
        tEnv.executeSql("CREATE CATALOG hive_catalog WITH (\n" +
                "\t'type' = 'hive',\n" +
                "\t'hive-conf-dir' = '"  + HIVE_CONF_DIR + "'" +
                ")");
        // 切换到hive的catelog
        tEnv.executeSql("USE CATALOG hive_catalog");
        System.out.println("getCurrentCatalog: " + tEnv.getCurrentCatalog());
        System.out.println("getCurrentDatabase: " + tEnv.getCurrentDatabase());

        // 向hive catalog对应的hive表插入数据FlinkImport
        tEnv.executeSql(sink_sql);
    }
}
