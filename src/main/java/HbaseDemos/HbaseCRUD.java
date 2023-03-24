package HbaseDemos;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Properties;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.BufferedMutator;
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
        hbase.getClusterInfos();
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
        this.config = HBaseConfiguration.create();
        // 第一种，直接加载配置文件，可以用于本地调试
        //this.config.addResource(new Path("C:\\BigDataEnv\\hbase-2.4.15\\conf\\hbase-site.xml"));
        //// 第二种，手动设置各个属性
        //this.config.set("hbase.master", "10.1.2.33:16000");  // 这个设不设置，似乎没啥影响
        this.config.set("hbase.zookeeper.property.clientPort", "2181");
        this.config.set("hbase.zookeeper.quorum", "10.1.2.33");  // master 所在服务器的IP或者主机名均可以
        //this.config.set("hbase.zookeeper.quorum", "hadoop101,hadoop102,hadoop103"); // 如果是集群，则主机名用逗号分隔
        // HBaseAdmin这个API在 2.x 版本中不能直接实例化了
        //this.hBaseAdmin = new HBaseAdmin(this.config);
        // 必须要通过如下的方式获取
        this.connection = ConnectionFactory.createConnection(config);
        this.admin =  connection.getAdmin();
    }

    public void close() throws IOException {
        this.admin.close();
        this.connection.close();
    }

    public void getClusterInfos() throws IOException {
        ClusterMetrics metrics = this.admin.getClusterMetrics();
        // 直接打印可以看到对应信息
        System.out.println(metrics);
        // 也可以获取指定信息
        String clusterId = metrics.getClusterId();
        String version = metrics.getHBaseVersion();
        ServerName masterName = metrics.getMasterName();
    }

    public void listTables() throws IOException {
        TableName[] list = this.admin.listTableNames();
        for (TableName tableName : list){
            System.out.print(tableName.getNameAsString() + ", ");
        }
    }

    public void getTableInfo(String tableName) throws IOException {
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
        colFamilies.forEach(col -> {
            System.out.println("add column family '" + col + "' to table '" + tableName + "'.");
            ColumnFamilyDescriptorBuilder colBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(col));
            colBuilder.setMaxVersions(1);
            // 使用 builder 来创建 列族描述符
            ColumnFamilyDescriptor colFamiliy = colBuilder.build();
            // 向表的builder对象中添加列族描述符
            tableBuilder.setColumnFamily(colFamiliy);
        });
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
        Scan scan=new Scan();
        scan.setLimit(limit);
        // 获取扫描结果
        ResultScanner scanner = table.getScanner(scan);
        System.out.println("scan table[" + tableName + "]...");
        for(Result result: scanner){
            System.out.print("result: ");
            System.out.println(result.toString());
            //得到单元格集合
            List<Cell> cs=result.listCells();
            System.out.println("result cells: ");
            for(Cell cell: cs){
                String rowKey=Bytes.toString(CellUtil.cloneRow(cell));
                long timestamp = cell.getTimestamp();
                String family = Bytes.toString(CellUtil.cloneFamily(cell));
                String qualifier  = Bytes.toString(CellUtil.cloneQualifier(cell));
                String value = Bytes.toString(CellUtil.cloneValue(cell));
                System.out.println(" ===> rowKey: " + rowKey + ",  timestamp: " + timestamp +
                        ", family: " + family + ", qualifier: " + qualifier + ", value: " + value);
            }
        }
        return true;
    }

}
