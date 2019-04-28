package KuduScalaAPI

import org.apache.kudu.ColumnSchema
import org.apache.kudu.Schema
import org.apache.kudu.Type
import org.apache.kudu.client._


class KuduUpdataDataAPI {

  // 创建kudu连接
  val kuduClient = new KuduClient.KuduClientBuilder("172.20.85.29:7051").build()

  // 设置表名
  val tableName = "kudu_test"

  // 获得表的连接
  val kuduTable = kuduClient.openTable(tableName)

  // 开启一个会话
  val  session = kuduClient.newSession()
  session.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH)

  // 创建updata对象
  val update = kuduTable.newUpdate()
  val rowUpdata = update.getRow()
  rowUpdata.addString("name", "nnnn")
  rowUpdata.addInt("age", 22)
  rowUpdata.addString("city", "ddddd")

  // 同步数据并关闭会话
  session.apply(update)
  session.flush()

  // 关闭kudu连接
  kuduClient.close()

}

object KuduUpdataDataAPI{
  def main(args: Array[String]): Unit = {

  }
}