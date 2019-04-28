package KuduScalaAPI

import org.apache.kudu.ColumnSchema
import org.apache.kudu.Schema
import org.apache.kudu.Type
import org.apache.kudu.client._

import scala.collection.JavaConverters._

class KuduSelectDataAPI {

  // 创建kudu连接
  val kuduClient = new KuduClient.KuduClientBuilder("datanode128:7051").build()

  // 设置表名
  val tableName = "user_info"

  // 获得表的连接
  val kuduTable = kuduClient.openTable(tableName)

  // 设置查询条件
  val schema = kuduTable.getSchema()
  val name = KuduPredicate.newComparisonPredicate(schema.getColumn("name"), KuduPredicate.ComparisonOp.EQUAL, "Ronnie")
  val age = KuduPredicate.newComparisonPredicate(schema.getColumn("age"), KuduPredicate.ComparisonOp.LESS, 22)
  val city = KuduPredicate.newComparisonPredicate(schema.getColumn("city"), KuduPredicate.ComparisonOp.EQUAL, "beijing")

  //columns.add(new ColumnSchema.ColumnSchemaBuilder("name", Type.STRING).compressionAlgorithm(ColumnSchema.CompressionAlgorithm.SNAPPY).build)

  // 执行查询操作
  val builder = kuduClient.newScannerBuilder(kuduTable)
    .setProjectedColumnNames(List("name", "age", "city").asJava)
    .addPredicate(name)
    .addPredicate(age)
    .addPredicate(city).build()
  while (builder.hasMoreRows()) {
    val results = builder.nextRows()
    while (results.hasNext()) {
      val result = results.next()
      System.out.println(result.getLong("name") + "_" + result.getInt("age") + "_" + result.getString("city"))
    }
  }

  // 关闭kudu连接
  kuduClient.close()
}

object KuduSelectDataAPI{
  def main(args: Array[String]): Unit = {

  }
}