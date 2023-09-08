package HbaseDemos;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// Hbase配置相关
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
// HBase集群元数据信息相关类
import org.apache.hadoop.hbase.ClusterMetrics;  // 这是个接口
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
// 管理API
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Admin;
// HBase表的元数据信息相关
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;  // 接口
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
// HBase CRUD 操作相关
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Result;  // 每一行记录的表示，内部是一个 Cell 数组
import org.apache.hadoop.hbase.client.BufferedMutatorParams;  // 批量操作时用到的封装类
import org.apache.hadoop.hbase.client.BufferedMutator;
// 工具类
import org.apache.hadoop.hbase.util.Bytes;  // 提供各种基本数据类型和 Byte 之间转换的工具类
import org.apache.hadoop.hbase.Cell;  // Hbase 中数据的最小单元，对应的是每一行数据里面的各个字段，比如 rowkey, qualifier 等的封装
import org.apache.hadoop.hbase.CellUtil;  // 访问 Cell 的一些工具类
// 下面的两个类都被划为内部使用的类了，不能直接实例化了
import org.apache.hadoop.hbase.client.HBaseAdmin;  // 这个类是 Admin 的实现类
import org.apache.hadoop.hbase.client.HTable;


public class HbaseCRUD {

    public static void main(String[] args) throws IOException {
        // Windows下需要设置 HADOOP_HOME 或者 hadoop.home.dir，否则会报错，但是好像报错也不影响实际执行
        // 下面这样设置环境变量没有用
        //System.setProperty("HBASE_DISABLE_HADOOP_CLASSPATH_LOOKUP", "true");
        //System.setProperty("HADOOP_HOME", "C:\\BigDataEnv\\hadoop-3.1.1-winutils");
        // 但是下面的配置会生效
        System.setProperty("hadoop.home.dir", "C:\\BigDataEnv\\hadoop-3.1.1-winutils");
        HbaseCRUD hbase = new HbaseCRUD();
        hbase.getClusterInfo();
        // 其他方法的测试见测试用例
    }

    public static void showEnvironmentVariables(){
        // 查看系统环境变量
        Map<String, String> map = System.getenv();
        map.forEach((k, v) -> {
            System.out.println("ENV : " + k + ", Value : " + v);
        });
    }

    private Configuration config;
    private Connection connection;
    private Admin admin;

    public HbaseCRUD() throws IOException{
        // 创建配置文件对象
        this.config = HBaseConfiguration.create();

        // 第一种，直接加载配置文件，可以用于本地调试
        //this.config.addResource(new Path("C:\\BigDataEnv\\hbase-2.4.15\\conf\\hbase-site.xml"));
        //this.config.addResource(new Path(System.getenv("HBASE_CONF_DIR"), "hbase-site.xml"));
        //this.config.addResource(new Path(System.getenv("HADOOP_CONF_DIR"), "core-site.xml"));

        // 第二种，手动设置各个属性
        //this.config.set("hbase.master", "10.8.6.185:16000");  // 这个设不设置，似乎没啥影响
        this.config.set("hbase.zookeeper.quorum", "10.8.6.185");  // master 所在服务器的IP或者主机名均可以
        //this.config.set("hbase.zookeeper.quorum", "hadoop101,hadoop102,hadoop103"); // 如果是集群，则主机名用逗号分隔
        this.config.set("hbase.zookeeper.property.clientPort", "2181");

        // HBaseAdmin这个API在 2.x 版本中不能直接实例化了
        //this.hBaseAdmin = new HBaseAdmin(this.config);  // HBase 1.x 版本
        // 必须要通过如下的方式获取
        this.connection = ConnectionFactory.createConnection(this.config);
        this.admin =  connection.getAdmin();
    }

    public void close() throws IOException {
        this.admin.close();
        this.connection.close();
    }

    public void getClusterInfo() throws IOException {
        ClusterMetrics metrics = this.admin.getClusterMetrics();
        // ClusterMetrics 是个接口，上面拿到的是它的一个代理实现类
        System.out.println(metrics.getClass().getName());
        // 直接打印可以看到对应信息
        System.out.println(metrics);
        // 也可以获取指定信息
        String clusterId = metrics.getClusterId();
        String version = metrics.getHBaseVersion();
        ServerName serverName = metrics.getMasterName();
        System.out.println(serverName + "-" + version + "-" + clusterId);
    }

    public void listTables() throws IOException {
        TableName[] list = this.admin.listTableNames();
        for (TableName tableName : list){
            System.out.print(tableName.getNameAsString() + ", ");
        }
    }

    public void getTableInfo(String tableName) throws IOException {
        // 实际使用table时，还必须要用 TableName.valueOf(tableName) 将表名封装成一个 POJO/Bean 对象...
        TableDescriptor tableDescriptor = this.admin.getDescriptor(TableName.valueOf(tableName));
        ColumnFamilyDescriptor[] colDesc = tableDescriptor.getColumnFamilies();
        System.out.println(tableDescriptor.getTableName());
        for(ColumnFamilyDescriptor col: colDesc){
            System.out.println(col.getNameAsString());
        }
    }

    public void createTable(String tableName, List<String> colFamilies) throws IOException {
        if(this.admin.tableExists(TableName.valueOf(tableName))){
            System.out.println("HTable '" + tableName + "' already exists, skip to create.");
            return;
        }
        // 创建表对象的builder
        TableDescriptorBuilder tableBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        // 用这个builder来添加列族等信息
        colFamilies.forEach(
            col -> {
                System.out.println("add column family '" + col + "' to table '" + tableName + "'.");
                ColumnFamilyDescriptorBuilder colBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(col));
                colBuilder.setMaxVersions(1);
                // 使用 builder 来创建 列族描述符
                ColumnFamilyDescriptor colFamiliy = colBuilder.build();
                // 向表的builder对象中添加列族描述符
                tableBuilder.setColumnFamily(colFamiliy);
            }
        );
        // 然后调用build方法得到创建好的TableDescriptor对象
        TableDescriptor tableDescriptor = tableBuilder.build();
        this.admin.createTable(tableDescriptor);
        System.out.println("create table '" + tableName + "' done.");
    }

    public boolean insertRow(String tableName, String rowKey, String colFamily, String col, String value) throws IOException{
        if(!this.admin.tableExists(TableName.valueOf(tableName))){
            System.out.println("HTable '" + tableName + "' does not exist, skip to insert.");
            close();
            return false;
        }
        // 必须要先获取 Table 接口引用的对象
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        // 从 HBase 1.1 开始， HTable对象就废弃了，包括其中的 .setAutoFlush() 方法，也就是不能通过上面的对象设置写缓冲了
        // 构建一个 Put 对象，用于后续封装一行的数据
        Put row = new Put(Bytes.toBytes(rowKey));
        row.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(value));
        table.put(row);
        table.close();
        System.out.println("put row '" + rowKey + "' into table[" + tableName + "] successfully.");
        return true;
    }

    public boolean batchInsertRows(String tableName, List<List<String>> rows) throws IOException{
        //第二种插入方式，也可批量插入
        //设置缓存1m，当达到1m时数据会自动刷到hbase，替代了HTable.setWriteBufferSize(30 * 1024 * 1024)
        BufferedMutatorParams params = new BufferedMutatorParams(TableName.valueOf(tableName));
        params.writeBufferSize(1024 * 1024);
        //创建一个批量异步与hbase通信的对象
        BufferedMutator mutator = connection.getBufferedMutator(params);
        String rowKey = null;
        String colFamily = null;
        String qualifier = null;
        String value = null;
        System.out.println("batch put into table[" + tableName + "] starts...");
        for(List<String> row: rows){
            rowKey = row.get(0);
            colFamily = row.get(1);
            qualifier = row.get(2);
            value = row.get(3);
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(qualifier), Bytes.toBytes(value));
            //向hbase插入数据,达到缓存会自动提交,这里也可以通过传入List<put>的方式批量插入
            mutator.mutate(put);
        }
        //不用每次put后就调用flush，最后调用就行，这个方法替代了旧api的HTable.setAutoFlush(false)
        mutator.flush();
        mutator.close();
        System.out.println("batch put into table[" + tableName + "] done.");
        return false;
    }

    public boolean scanTable(String tableName, int limit) throws IOException {
        if(!this.admin.tableExists(TableName.valueOf(tableName))) {
            System.out.println("HTable '" + tableName + "' does not exist, skip to insert.");
            return false;
        }
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        // 创建扫描器，并添加条件
        Scan scan = new Scan();
        scan.setLimit(limit);
        // 获取扫描结果
        ResultScanner scanner = table.getScanner(scan);
        System.out.println("scan table[" + tableName + "]...");
        for(Result result: scanner){
            // Result 是每一行记录的封装，每个字段的信息以 Cell 的形式封装在其中，所以 Result 内部主要是一个 Cell 数组
            System.out.print("result: ");
            System.out.println(result.toString());
            // 得到单元格集合
            List<Cell> cs = result.listCells();
            System.out.println("result cells: ");
            for(Cell cell: cs) {
                // 直接取值，拿到的 byte[] 好像有问题
                //String rowKey = Bytes.toString(cell.getRowArray());
                //String qualifier  = Bytes.toString(cell.getQualifierArray());
                //String family = Bytes.toString(cell.getFamilyArray());
                //String value = Bytes.toString(cell.getValueArray());
                // 使用 CellUtil 才能拿到正确的值
                String rowKey = Bytes.toString(CellUtil.cloneRow(cell));
                String qualifier  = Bytes.toString(CellUtil.cloneQualifier(cell));
                String family = Bytes.toString(CellUtil.cloneFamily(cell));
                String value = Bytes.toString(CellUtil.cloneValue(cell));
                long timestamp = cell.getTimestamp();
                System.out.println(" ===> rowKey: {" + rowKey + ", family: " + family + ", qualifier: " + qualifier
                        + ", value: " + value + ",  timestamp: " + timestamp + "}");
            }
        }
        return true;
    }

}
