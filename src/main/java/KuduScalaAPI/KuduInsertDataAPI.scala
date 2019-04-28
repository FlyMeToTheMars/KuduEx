package KuduScalaAPI

import org.apache.kudu.ColumnSchema
import org.apache.kudu.Schema
import org.apache.kudu.Type
import org.apache.kudu.client._


class KuduInsertDataAPI {

  // 创建kudu连接
  val kuduClient = new KuduClient.KuduClientBuilder("172.20.85.29:7051").build()

  // 设置表名
  val tableName = "kudu_test"

  // 获得表的连接
  val kuduTable = kuduClient.openTable(tableName)

  // 开启一个会话
  val  session = kuduClient.newSession()
  session.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH)

  // 创建插入对象并设置插入数据
  val insert = kuduTable.newInsert()
  val row = insert.getRow()
  row.addString(0, "Ronnie")
  row.addInt(1, 21)
  row.addString(2, "beijing")

  //执行插入语句
  session.apply(insert)

  // 同步数据，关闭会话
  session.flush()
  session.close()

}

object KuduInsertDataAPI{
  def main(args: Array[String]): Unit = {

  }
}