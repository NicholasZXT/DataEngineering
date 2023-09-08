import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import HbaseDemos.HbaseCRUD;

public class HBaseCRUDTest {
    private HbaseCRUD hbase;

    @Before
    public void windowsConfig() throws IOException {
        System.out.println(this.getClass().getSimpleName() + " Junit @Before...");
        // Windows下需要配置这个
        System.setProperty("hadoop.home.dir", "C:\\BigDataEnv\\hadoop-3.1.1-winutils");
        hbase = new HbaseCRUD();
    }

    @After
    public void close() throws IOException{
        System.out.println(this.getClass().getSimpleName() + " Junit @After...");
        hbase.close();
    }

    @Test
    public void getClusterInfosTest() throws IOException {
        hbase.getClusterInfo();
    }

    @Test
    public void listTablesTest() throws IOException {
        hbase.listTables();
    }

    @Test
    public void getTableInfoTest() throws IOException {
        hbase.getTableInfo("table1");
    }

    @Test
    public void createTableTest() throws IOException {
        ArrayList<String> colFamilies = new ArrayList<>();
        colFamilies.add("cf1");
        colFamilies.add("cf2");
        hbase.createTable("table2", colFamilies);
    }

    @Test
    public void insertRowTest() throws IOException {
        String tableName = "table2";
        String rowKey = null;
        String colFamily = null;
        String qualifier = null;
        String value = null;
        boolean flag = false;
        List<List<String>> rows = new ArrayList<>();
        rows.add(new ArrayList<>(Arrays.asList("row-1", "cf1", "q1", "q1-v1")));
        rows.add(new ArrayList<>(Arrays.asList("row-1", "cf1", "q2", "q2-v1")));
        rows.add(new ArrayList<>(Arrays.asList("row-1", "cf2", "q2", "q2-v2")));
        rows.add(new ArrayList<>(Arrays.asList("row-2", "cf1", "q1", "q1-v2")));
        rows.add(new ArrayList<>(Arrays.asList("row-2", "cf2", "q2", "q2-v3")));
        for(List<String> row: rows){
            rowKey = row.get(0);
            colFamily = row.get(1);
            qualifier = row.get(2);
            value = row.get(3);
            flag = hbase.insertRow(tableName, rowKey, colFamily, qualifier, value);
        }
    }

    @Test
    public void batchInsertRowsTest() throws IOException {
        String tableName = "table2";
        List<List<String>> rows = new ArrayList<>();
        rows.add(new ArrayList<>(Arrays.asList("row-3", "cf1", "q3", "q3-v1")));
        rows.add(new ArrayList<>(Arrays.asList("row-3", "cf2", "q3", "q3-v2")));
        rows.add(new ArrayList<>(Arrays.asList("row-3", "cf2", "q4", "q4-v1")));
        rows.add(new ArrayList<>(Arrays.asList("row-4", "cf1", "q4", "q4-v2")));
        rows.add(new ArrayList<>(Arrays.asList("row-4", "cf2", "q5", "q5-v1")));
        hbase.batchInsertRows(tableName, rows);
    }

    @Test
    public void scanTableTest() throws IOException {
        String tableName = "table2";
        hbase.scanTable(tableName, 20);
    }
}
