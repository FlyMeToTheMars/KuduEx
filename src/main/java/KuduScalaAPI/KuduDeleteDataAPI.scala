package KuduScalaAPI

import org.apache.kudu.ColumnSchema
import org.apache.kudu.Schema
import org.apache.kudu.Type
import org.apache.kudu.client._


class KuduDeleteDataAPI {
  // 创建kudu连接
  val kuduClient = new KuduClient.KuduClientBuilder("172.20.85.29:7051").build()

  // 设置表名
  val tableName = "kudu_test"

  // 获得表的连接
  val kuduTable = kuduClient.openTable(tableName)

  // 开启一个会话
  val  session = kuduClient.newSession()
  session.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH)

  // 创建删除对象并指定要删除的行
  val delete = kuduTable.newDelete()
  delete.getRow().addString("name", "Ronnie")

  // 执行删除操作
  session.apply(delete)

  // 同步数据并关闭会话
  session.flush()
  session.close()

  // 关闭kudu连接
  kuduClient.close()
}

object KuduDeleteDataAPI{
  def main(args: Array[String]): Unit = {

  }
}
