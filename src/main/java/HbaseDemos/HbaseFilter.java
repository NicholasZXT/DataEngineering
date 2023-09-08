package HbaseDemos;

import java.io.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.BinaryComparator;


public class HbaseFilter {
    public static void main(String[] args) throws IOException {
        String filepath = "companies.txt";
        String outpath = "patents.txt";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outpath, true));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HbaseFilter patentsExporting = new HbaseFilter();
        patentsExporting.setConfig();

        List<String> companies = patentsExporting.readCompanies(filepath);
        //List<String> companies = Arrays.asList("科大讯飞", "科大国创");
        //List<String> companies = Collections.singletonList("科大讯飞");
        //List<String> companies = Collections.singletonList("安徽科大讯飞信息科技股份有限公司");

        patentsExporting.extractByAccurateFilter(dateFormat, companies, writer, -1);
        //patentsExporting.extractByRegexFilter(dateFormat, companies, writer, 5);

        writer.close();
    }

    private Configuration config;
    private Connection connection;
    private Admin admin;

    public void setConfig() throws IOException {
        this.config = HBaseConfiguration.create();
        this.config.set("hbase.zookeeper.property.clientPort", "2181");
        this.config.set("hbase.zookeeper.quorum", "hadoop101,hadoop102,hadoop103");  // master 所在服务器的IP或者主机名均可以
        // 必须要通过如下的方式获取 Admin 客户端
        this.connection = ConnectionFactory.createConnection(config);
        this.admin =  connection.getAdmin();
    }

    public List<String> readCompanies(String filepath) throws IOException {
        FileReader fileReader = new FileReader(filepath);
        BufferedReader reader = new BufferedReader(fileReader);
        reader.readLine();
        List<String> companies = new ArrayList<>();
        String line = reader.readLine();
        while (line != null){
            if (line.length() > 0) {
                System.out.println(line);
                companies.add(line);
            }
            line = reader.readLine();
        }
        return companies;
    }

    public void extractByAccurateFilter(SimpleDateFormat dateFormat, List<String> companies, BufferedWriter writer, int limit)
            throws IOException {
        String tableName = "patent:patent_info_alter_ah_extra";
        String colFamily = "cf";
        String[] columns = {"txtTitle", "txtApplicant", "txtDescTitle", "txtDescPara", "txtAbsPara"};
        writer.write("company");
        writer.write('\t');
        writer.write("rowkey");
        writer.write('\t');
        writer.write("extra:applicant_Name");
        for (String col: columns){
            writer.write('\t');
            writer.write(colFamily + ":" + col);
        }
        writer.newLine();
        writer.flush();
        for (String company: companies){
            System.out.println("----- [" + dateFormat.format(new Date()) + "] processing company: " + company + " -----");
            ResultScanner scanner = this.accurateFilterByApplicant(tableName, colFamily, columns, "applicant_Name", company, limit);
            int count = 0;
            for(Result result: scanner){
                String rowkey = Bytes.toString(result.getRow());
                writer.write(company);
                writer.write('\t');
                writer.write(rowkey);
                System.out.println("[" + dateFormat.format(new Date()) + "] get data for company [" + company + "] with rowkey: " + rowkey);
                String applicant_name = Bytes.toString(result.getValue(Bytes.toBytes("extra"), Bytes.toBytes("applicant_Name")));
                writer.write('\t');
                writer.write(applicant_name);
                for(String col: columns) {
                    byte[] data_bytes = result.getValue(Bytes.toBytes(colFamily), Bytes.toBytes(col));
                    String data = Bytes.toString(data_bytes);
                    if (data != null){
                        count += 1;
                        writer.write('\t');
                        writer.write(data);
                        writer.flush();
                    }
                }
                writer.newLine();
            }
            System.out.println("----- [" + dateFormat.format(new Date()) + "] get data done for company [" + company + "] with count: " + count);
        }
    }

    public void extractByRegexFilter(SimpleDateFormat dateFormat, List<String> companies, BufferedWriter writer, int limit)
            throws IOException{
        String tableName = "patent:patent_info_alter_ah";
        String colFamily = "cf";
        String[] columns = {"txtTitle", "txtApplicant", "txtDescTech", "txtAbsPara"};
        writer.write("company");
        writer.write('\t');
        writer.write("rowkey");
        for (String col: columns){
            writer.write('\t');
            writer.write(colFamily + ":" + col);
        }
        writer.newLine();
        writer.flush();
        for (String company: companies){
            System.out.println("----- [" + dateFormat.format(new Date()) + "] processing company: " + company + " -----");
            ResultScanner scanner = this.regexFilterByApplicant(tableName, colFamily, columns, "txtApplicant", company, limit);
            int count = 0;
            for(Result result: scanner){
                String rowkey = Bytes.toString(result.getRow());
                writer.write(company);
                writer.write('\t');
                writer.write(rowkey);
                System.out.println("[" + dateFormat.format(new Date()) + "] get data for company [" + company + "] with rowkey: " + rowkey);
                for(String col: columns){
                    byte[] data_bytes = result.getValue(Bytes.toBytes(colFamily), Bytes.toBytes(col));
                    String data = Bytes.toString(data_bytes);
                    if (data != null){
                        count += 1;
                        writer.write('\t');
                        writer.write(data);
                        writer.flush();
                    }
                }
                writer.newLine();
            }
            System.out.println("----- [" + dateFormat.format(new Date()) + "] get data done for company [" + company + "] with count: " + count);
        }
    }

    public ResultScanner accurateFilterByApplicant(String tableName, String colFamily, String[] columns, String col_filter, String applicant, int limit)
            throws IOException{
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        Scan scan=new Scan();
        if (limit > 0) scan.setLimit(limit);
        for (String col: columns){
            scan.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
        }
        scan.addColumn(Bytes.toBytes("extra"), Bytes.toBytes(col_filter));
        // 使用准确匹配
        BinaryComparator comparator = new BinaryComparator(Bytes.toBytes(applicant));
        //SubstringComparator comparator = new SubstringComparator(applicant);
        Filter filter = new SingleColumnValueFilter(Bytes.toBytes("extra"), Bytes.toBytes(col_filter),
                CompareOperator.EQUAL, comparator);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        return scanner;
    }

    public ResultScanner regexFilterByApplicant(String tableName, String colFamily, String[] columns, String col_filter, String applicant, int limit)
            throws IOException{
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        Scan scan=new Scan();
        //scan.setBatch(100);
        if (limit > 0) scan.setLimit(limit);
        for (String col: columns){
            scan.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
        }
        String regex_string = ".*" + applicant + ".*";
        RegexStringComparator regexStringComparator = new RegexStringComparator(regex_string);
        Filter filter = new SingleColumnValueFilter(Bytes.toBytes(colFamily), Bytes.toBytes(col_filter),
                CompareOperator.EQUAL, regexStringComparator);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        return scanner;
    }


    public void scanTable() throws IOException {
        this.setConfig();
        String tableName = "idata:patent_info_alter_ah";
        Table table = this.connection.getTable(TableName.valueOf(tableName));
        Scan scan=new Scan();
        scan.setLimit(5);
        ResultScanner scanner = table.getScanner(scan);
        System.out.println("scan table[" + tableName + "]...");
        for(Result result: scanner){
            List<Cell> cs = result.listCells();
            System.out.println("result cells: ");
            for(Cell cell: cs){
                String rowKey = Bytes.toString(CellUtil.cloneRow(cell));
                String family = Bytes.toString(CellUtil.cloneFamily(cell));
                String qualifier  = Bytes.toString(CellUtil.cloneQualifier(cell));
                String value = Bytes.toString(CellUtil.cloneValue(cell));
                long timestamp = cell.getTimestamp();
                System.out.println(" ===> rowKey: {" + rowKey + ", family: " + family + ", qualifier: " + qualifier +
                        ", value: " + value + ",  timestamp: " + timestamp + "}");
            }
        }
    }
}
