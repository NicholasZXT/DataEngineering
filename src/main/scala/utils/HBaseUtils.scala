package utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Connection, Admin}
//HBaseAdmin, HTable,
//  TableDescriptorBuilder, TableDescriptor,
//  ColumnFamilyDescriptorBuilder, ColumnFamilyDescriptor
//}

object HBaseUtils {
  def main(args: Array[String]): Unit = {
    val zooKeeperServer: String = "10.8.6.185"
    val hbase = new HBaseUtils(zooKeeperServer)
  }
}

class HBaseUtils private() {
  private var conf: Configuration = _
  private var connection: Connection = _
  private var admin: Admin = _

  def config: Configuration = conf

  def admin_client: Admin = admin

  def this(zookeeper_servers: String) = {
    this()
    this.conf = HBaseConfiguration.create()
    this.conf.set("hbase.zookeeper.quorum", zookeeper_servers)
    this.conf.set("hbase.zookeeper.property.clientPort", "2181")
    this.connection = ConnectionFactory.createConnection(this.conf)
    this.admin = connection.getAdmin
  }

  def close(): Unit = {
    this.admin.close()
    this.connection.close()
  }

}
