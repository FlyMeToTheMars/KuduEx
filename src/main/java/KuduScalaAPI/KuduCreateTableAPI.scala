package KuduScalaAPI

import org.apache.kudu.ColumnSchema
import org.apache.kudu.Schema
import org.apache.kudu.Type
import org.apache.kudu.client._


import scala.collection.JavaConverters._


class KuduCreateTableAPI {

  // 创建kudu连接
  val kuduClient = new KuduClient.KuduClientBuilder("172.20.85.29:7051").build()

  // 设置表名
  val tableName = "kudu_test"

  // 创建列
  val colums = List[ColumnSchema]((new ColumnSchema.ColumnSchemaBuilder("name", Type.STRING).key(true).nullable(false).build()),
    (new ColumnSchema.ColumnSchemaBuilder("age", Type.INT64).nullable(true).build()),
    (new ColumnSchema.ColumnSchemaBuilder("city", Type.STRING).nullable(true).build()))
  val schema: Schema = new Schema(colums.asJava)

  // 设置hash分区
  val cto: CreateTableOptions = new CreateTableOptions()
  cto.setRangePartitionColumns(List("name").asJava).setNumReplicas(3)

  // 执行建表语句
  kuduClient.createTable(tableName, schema, cto)

  // 关闭kudu连接
  kuduClient.close()

}

object KuduCreateTableAPI{
  def main(args: Array[String]): Unit = {

  }
}