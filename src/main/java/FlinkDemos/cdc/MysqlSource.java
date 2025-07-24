package FlinkDemos.cdc;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.CheckpointingMode;
// Flink-CDC的 2.0.0 ~ 3.0.1 版本使用的是如下包名空间
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
// Flink-CDC的 3.1.x 以上版本使用的是如下包名空间
//import org.apache.flink.cdc.connectors.mysql.source.MySqlSource;
//import org.apache.flink.cdc.connectors.mysql.table.StartupOptions;
//import org.apache.flink.cdc.debezium.JsonDebeziumDeserializationSchema;

/**
 *
 * CREATE TABLE `person` (
 *   `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
 *   `name` varchar(255) DEFAULT NULL,
 *   `age` int DEFAULT NULL,
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
 */

public class MysqlSource {
    public static void main(String[] args) throws Exception {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path checkpointDir = currentDir.resolve("cdc-checkpoint");

        //1.获取Flink执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //2.开启CheckPoint
        env.enableCheckpointing(5000L);
        env.getCheckpointConfig().setCheckpointTimeout(10000L);
        // 下面有个重载的 Path 参数方法，不过那个 Path 不是 java.nio.file.Path 对象
        env.getCheckpointConfig().setCheckpointStorage(checkpointDir.toUri());
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

        //3.使用FlinkCDC构建MySQLSource
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .username("root")
                .password("psw")
                .databaseList("flink_cdc")
                .tableList("flink_cdc.person") //在写表时，需要带上库名。如果什么都不写，则表示监控所有的表
                .startupOptions(StartupOptions.initial())
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

        //4.读取数据
        DataStreamSource<String> mysqlDS = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "mysql-source");

        //5.打印
        mysqlDS.print();
        // 输出示例如下：
        /**
         * 插入数据 {"id":1,"name":"nicho","age":26}
         * {
         *     "before":null,
         *     "after":{"id":1,"name":"nicho","age":26},
         *     "source":{"version":"1.9.7.Final","connector":"mysql","name":"mysql_binlog_source","ts_ms":1753321567000,"snapshot":"false","db":"flink_cdc","sequence":null,"table":"person","server_id":1,"gtid":null,"file":"LAPTOP-JK0VU7KO-bin.000014","pos":435103,"row":0,"thread":9,"query":null},
         *     "op":"c",
         *     "ts_ms":1753321567992,
         *     "transaction":null
         * }
         *
         * 修改数据 {"id":1,"name":"nicho","age":26} 为  {"id":1,"name":"nicho","age":27}
         * {
         *     "before":{"id":1,"name":"nicho","age":26},
         *     "after":{"id":1,"name":"nicho","age":27},
         *     "source":{"version":"1.9.7.Final","connector":"mysql","name":"mysql_binlog_source","ts_ms":1753321589000,"snapshot":"false","db":"flink_cdc","sequence":null,"table":"person","server_id":1,"gtid":null,"file":"LAPTOP-JK0VU7KO-bin.000014","pos":435419,"row":0,"thread":9,"query":null},
         *     "op":"u",
         *     "ts_ms":1753321589748,
         *     "transaction":null
         * }
         *
         * 插入数据 {"id":2,"name":"jane","age":18}
         * {
         *     "before":null,
         *     "after":{"id":2,"name":"jane","age":18},
         *     "source":{"version":"1.9.7.Final","connector":"mysql","name":"mysql_binlog_source","ts_ms":1753321607000,"snapshot":"false","db":"flink_cdc","sequence":null,"table":"person","server_id":1,"gtid":null,"file":"LAPTOP-JK0VU7KO-bin.000014","pos":435743,"row":0,"thread":9,"query":null},
         *     "op":"c",
         *     "ts_ms":1753321607379,
         *     "transaction":null
         * }
         *
         * 删除数据 {"id":2,"name":"jane","age":18}
         * {
         *     "before":{"id":2,"name":"jane","age":18},
         *     "after":null,
         *     "source":{"version":"1.9.7.Final","connector":"mysql","name":"mysql_binlog_source","ts_ms":1753321616000,"snapshot":"false","db":"flink_cdc","sequence":null,"table":"person","server_id":1,"gtid":null,"file":"LAPTOP-JK0VU7KO-bin.000014","pos":436049,"row":0,"thread":9,"query":null},
         *     "op":"d",
         *     "ts_ms":1753321616792,
         *     "transaction":null
         * }
         */

        //6.启动
        env.execute();
    }
}
