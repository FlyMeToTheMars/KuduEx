package kudu;

import com.cloudera.RandomUserInfo;
import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.*;
import java.util.ArrayList;
import java.util.List;


public class KuduUtils {

    /**
     * 使用Kudu API创建一个Kudu表
     * @param client
     * @param tableName
     */
    public static void createTable(KuduClient client, String tableName) {
        List<ColumnSchema> columns = new ArrayList();
        //在添加列时可以指定每一列的压缩格式
        columns.add(new ColumnSchema.ColumnSchemaBuilder("id", Type.STRING).key(true).        compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build());
        //......


        Schema schema = new Schema(columns);
        CreateTableOptions createTableOptions = new CreateTableOptions();
        List<String> hashKeys = new ArrayList();
        hashKeys.add("id");
        int numBuckets = 8;
        createTableOptions.addHashPartitions(hashKeys, numBuckets);

        try {
            if(!client.tableExists(tableName)) {
                client.createTable(tableName, schema, createTableOptions);
            }
            System.out.println("成功创建Kudu表：" + tableName);
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向指定的Kudu表中upsert数据，数据存在则更新，不存在则新增
     * @param client KuduClient对象
     * @param tableName 表名
     * @param numRows 向表中插入的数据量
     */
    public static void upsert(KuduClient client, String tableName, int numRows ) {
        try {
            KuduTable kuduTable = client.openTable(tableName);
            KuduSession kuduSession = client.newSession();
            //设置Kudu提交数据方式，这里设置的为手动刷新，默认为自动提交
            //


            kuduSession.flush();
            kuduSession.close();
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看Kudu表中数据
     * @param client
     * @param tableName
     */
    public static void scanerTable(KuduClient client, String tableName) {
        try {
            KuduTable kuduTable = client.openTable(tableName);
            KuduScanner kuduScanner = client.newScannerBuilder(kuduTable).build();
            while(kuduScanner.hasMoreRows()) {
                RowResultIterator rowResultIterator =kuduScanner.nextRows();
                while (rowResultIterator.hasNext()) {
                    RowResult rowResult = rowResultIterator.next();
                    System.out.println(rowResult.getString("id"));
                }
            }
            kuduScanner.close();
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除表
     * @param client
     * @param tableName
     */
    public static void dropTable(KuduClient client, String tableName) {
        try {
            client.deleteTable(tableName);
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出Kudu下所有的表
     * @param client
     */
    public static void tableList(KuduClient client) {
        try {
            ListTablesResponse listTablesResponse = client.getTablesList();
            List<String> tblist = listTablesResponse.getTablesList();
            for(String tableName : tblist) {
                System.out.println(tableName);
            }
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

}