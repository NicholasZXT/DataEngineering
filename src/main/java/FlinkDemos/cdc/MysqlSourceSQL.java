package FlinkDemos.cdc;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.Table;

public class MysqlSourceSQL {

        public static void main(String[] args) {
        //1.获取Flink执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //2.使用FlinkCDC SQL方式建表
        tableEnv.executeSql(
                "create table person(\n" +
                "    id int primary key NOT ENFORCED,\n" +
                "    name string," +
                "    age int" +
                ") WITH (\n" +
                " 'connector' = 'mysql-cdc',\n" +
                " 'hostname' = 'localhost',\n" +
                " 'port' = '3306',\n" +
                " 'username' = 'root',\n" +
                " 'password' = 'psw',\n" +
                " 'database-name' = 'flink_cdc',\n" +
                " 'table-name' = 'person'\n" +
                ")");

        //3.查询并打印
        Table table = tableEnv.sqlQuery("select * from person");
        table.execute().print();
        // 输出示例：
        //  +----+-------------+--------------------------------+-------------+
        //  | op |          id |                           name |         age |
        //  +----+-------------+--------------------------------+-------------+
        //  | +I |           3 |                          nicho |          26 | 插入数据 {"id":3,"name":"nicho","age":26}
        //  | -U |           3 |                          nicho |          26 | 修改数据 {"id":3,"name":"nicho","age":26} 为  {"id":3,"name":"nicho","age":29}
        //  | +U |           3 |                          nicho |          29 |
        //  | -U |           3 |                          nicho |          29 | 修改数据 {"id":3,"name":"nicho","age":29} 为  {"id":3,"name":"nicho","age":30}
        //  | +U |           3 |                          nicho |          30 |
        //  | +I |           4 |                           jane |          18 | 插入数据 {"id":4,"name":"jane","age":18}
        //  | -D |           4 |                           jane |          18 | 删除数据 {"id":4,"name":"jane","age":18}

    }
}
