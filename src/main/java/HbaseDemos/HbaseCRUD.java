package HbaseDemos;
import java.io.IOException;
import java.util.List;
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
// 下面的两个类都被划为内部使用的类了，不能直接实例化了
import org.apache.hadoop.hbase.client.HBaseAdmin;  // 这个类是 Admin 的实现类
import org.apache.hadoop.hbase.client.HTable;


public class HbaseCRUD {
    private Configuration config;
    private Connection connection;
    private Admin admin;

    public HbaseCRUD() throws IOException{
        this.config = HBaseConfiguration.create();
        this.config.addResource(new Path("C:\\BigDataEnv\\hbase-2.4.15\\conf\\hbase-site.xml"));
        // HBaseAdmin这个API在 2.x 版本中不能直接实例化了
        //this.hBaseAdmin = new HBaseAdmin(this.config);
        // 必须要通过如下的方式获取
        this.connection = ConnectionFactory.createConnection(config);
        this.admin =  connection.getAdmin();
    }

    public void getClusterInfos() throws IOException {
        ClusterMetrics metrics = this.admin.getClusterMetrics();
        String clusterId = metrics.getClusterId();
        String version = metrics.getHBaseVersion();
        ServerName masterName = metrics.getMasterName();
        System.out.println(metrics);
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
            System.out.println("HTable " + tableName + " already exists, skip to create.");
            return;
        }
        // 创建表对象的builder
        TableDescriptorBuilder tableBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        // 用这个builder来添加列族等信息
        colFamilies.forEach(col -> {
            ColumnFamilyDescriptorBuilder colBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(col));
            colBuilder.setMaxVersions(1);
            ColumnFamilyDescriptor colFamiliy = colBuilder.build();
            tableBuilder.setColumnFamily(colFamiliy);
        });
        // 然后调用build方法得到创建好的TableDescriptor对象
        TableDescriptor tableDescriptor = tableBuilder.build();
        this.admin.createTable(tableDescriptor);
    }

}
