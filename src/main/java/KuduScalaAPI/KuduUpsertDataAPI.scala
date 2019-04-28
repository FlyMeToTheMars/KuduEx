package KuduScalaAPI

import org.apache.kudu.ColumnSchema
import org.apache.kudu.Schema
import org.apache.kudu.Type
import org.apache.kudu.client._


class KuduUpsertDataAPI {

  // 创建kudu连接
  val kuduClient = new KuduClient.KuduClientBuilder("172.20.85.29:7051").build()

  // 设置表名
  val tableName = "kudu_test"

  // 获得表的连接
  val kuduTable = kuduClient.openTable(tableName)

  // 开启一个会话
  val  session = kuduClient.newSession()
  session.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH)

  // 创建upsert对象
  val upsert = kuduTable.newUpsert()
  val rowUpsert = upsert.getRow()
  rowUpsert.addString("name", "nnnn")
  rowUpsert.addInt("age", 19)
  rowUpsert.addString("city", "mmmm")

  // 执行upsert操作
  session.apply(upsert)

  // 同步数据并关闭会话
  session.flush()
  session.close()

  // 关闭kudu连接
  kuduClient.close()

  // 关闭kudu连接
  kuduClient.close()

}

object KuduUpsertDataAPI{
  def main(args: Array[String]): Unit = {

  }
}