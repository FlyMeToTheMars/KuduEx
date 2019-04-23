## Kudu笔记

### 1.Apache Kudu介绍

> 部分内容翻译自Kudu 1.7.1文档

Kudu是为Apachche Hadoop平台开发的柱状存储管理器，Kudu有Hadoop生态系统应用的普遍技术属性：运行在商业硬件上，可以横向拓展，支持高可用操作。

Kudu的优点包括：

* 快速处理OLAP（联机分析处理）工作负载
* 与MR、Spark和其他Hadoop生态组件整合
* 与Apache Impala紧密结合，使它成为在Apache Parquet使用HDFS的一个好的，可变的替代方案
* 强大但灵活的一致性迷行，允许你根据每个请求选择一致性要求，包括严格可序列化一致性选项
* 同时运行顺序和随机工作负载的强大性能
* 对于管理员，使用Cloudera Manager容易管理
* 高可用、Tablet服务和Master使用共识算法，保证只要超过一半的副本可用，table就可以读写
* 结构化数据模型

通过结合这些特性，Kudu出现的目的是为了解决当前hadoop技术难以达成的应用场景，例如：

* 新到达的数据要被立刻提供给用户报表
* 时间序列应用必须同时支持的：大量历史数据的查询的要求、对某个细颗粒查询迅速返回的时间要求
* 在基于所有历史数据来做出实时决策的预测模型上的应用

### 2.Kudu与Impala在CDH中的集成

![Az1eTx.png](https://s2.ax1x.com/2019/04/17/Az1eTx.png)

如图只要在impala的配置中配置该项目，配置项目为kudu_master_hosts，配置内容为kudu的master节点，端口号默认为7051

配置完成之后简单测试：

在HUE中调用impala的输入框中输入：

```sql
CREATE TABLE my_first_table 
( 
    id BIGINT, name STRING, PRIMARY KEY(id) 
) 
PARTITION BY HASH PARTITIONS 16 
STORED AS KUDU
```

即可建立表格。

--------------------------

```sql
insert into my_first_table values (99,"sarah");
```

```mysql
INSERT INTO  my_first_table VALUES (1, "john"), (2, "jane"), (3,  "jim");
```

插入语句

----------------------------

```sql
select * from  my_first_table;
```

查表

---

```sql
delete from  my_first_table where id =99;
```

删除语句

---

```sql
update my_first_table  set name='lilei' where id=99;
```

改语句

---

```sql
upsert  into my_first_table values(1,  "john"), (4, "tom"), (99, "lilei1");
```

Kudu中的upsert是update和insert的结合体，有更新就更新，没有该条数据就插入。

这边联想到mongo中的upsert，MongoDB 的update 方法的三个参数是upsert，这个参数是个布尔类型，默认是false。当它为true的时候，update方法会首先查找与第一个参数匹配的记录，在用第二个参数更新之。

```sql
db.post.update({count:100},{"$inc":{count:10}},true);
```

在找不到count=100这条记录的时候，自动插入一条count=100，然后再加10，最后得到一条 count=110的记录。

#### Kudu-Impala整合特征

CREATE/ALTER/DROP TABLE

Impala支持使用Kudu作为持久层创建、修改和删除表，这些表和Impala其他表使用同样的内部和外部方法，允许复杂的数据整合和查询。

INSERT

可以使用通用语法插入数据到Kudu-Imapala表格

UPDATE/DELETE

支持SQL命令逐行或者批量修改Kudu表中存在的数据。SQL的语法尽可能与现有的相同。

**Flexible Partitioning**

和Hive表类似，Kudu也支持hash或者range预先动态分区，以便在集群中均匀读写。你可以通过很多方式分区，比如说任意列的主键，任意数量的哈希。

**并行扫描**

为了在现在硬件上达到最高可用性能，Impala使用Kudu并行扫描多个客户端。

**高效查询**

可能的时候，Impala会吧谓词评估推送给kudu,以便尽可能接近数据，在很多工作下，查询性能和Parquet相当。

更多细节需要查询Impala文档。

---

### 3. 概念和术语

**Columnar Data Store**

*列存储*

Kudu是列存储，列存储是强类型的，通过适当的设计，相对于数仓是优越的，因为几个原因。

**Read Efficiency**

*读取效率*

对于分析查询，可以单独读取一列，或者一列的一部分，而忽略其他列。这意味着你可以在磁盘上读取最小的数据来满足查询。

**Data Compression**

*数据压缩*

因为是强类型，所以数据的压缩比要比基于行的解决方案效率高几个数量级。

**Table**

Table是数据存储在Kudu的位置，Table有schema和一个完全有序的主键，一个表被分割成叫做tablets的segment。

**Tablet**

首先，tablet是table的segment，类似于partition 机制。Tablet里面本身还涉及副本机制等。

**Tablet Server**

对于Tablet的副本机制来说，每个tablet都会有一个leader，别的是follower，leaders会接收读和写请求，followers只接受读请求。涉及到副本机制就涉及到共识算法，这里面用的共识算法是Raft Consensus Algorithm。

**Master**

Master跟踪tablets，tablet Server，Catalog Table 以及和Cluster关联的元数据，只能有一个master，如果挂了，那么会用Raft Consensus Algorithm重新选举一个出来。

*Raft Consensus Algorithm*

和Paxos一样是一个重要的共识算法

*Catalog Table*

Catalog Table是matadata的核心部位，记录table和tablets的信息，不能直接读写，只能通过Client API进行改动，存储两种类型的数据。

Tables：table的schemas, locations, and states

Tablets：tablets的list，tablet Server的映射关系，states，start and end keys

*Logical Replication*

逻辑复制：Kudu复制文件仅仅是逻辑上的复制，并没有涉及物理上的复制，这样做有几个好处：

* 尽管插入和更新操作通过网络传输数据，删除操作不需要移动任何数据。删除操作被送到每一个执行本地删除操作的tablet server。

* 压缩这样的物理操作，不需要通过网络传输，这和使用HDFS的存储系统是不同的，HDFS中的blocks需要通过网络传输block来满足复制的需求。

* Tablets不需要在同一时间压缩，或者其他在物理层上的同步，这降低了tablet因为大量压缩和写入遇到延迟的可能性。

  

---

### 4.Java访问CDH集群中的Kudu

如果使用的不是局域网，跨网段需要进行配置。

需要在KuduMaster服务的高级配置“gflagfile 的 Master 高级配置代码段（安全阀）”增加配置

```--trusted_subnets=0.0.0.0/0```

这个是受信人的子网名单，设置为0000代表允许所有IP没有经过验证/未加密的连接

如果没有配置这个名单，会报错：

```java
W1128 16:56:55.749083 93981 negotiation.cc:318] Unauthorized connection attempt: Server connection negotiation failed: server connection from 100.73.0.57:42533: unauthenticated connections from publicly routable IPs are prohibited. See --trusted_subnets flag for more information
```

## 

### 5.架构概述

下图显示了一个Kudu集群，三个Master和多个table servers，每一个Server服务多个tablets。

这个图表明了Raft共识算法是如何作用在master-tablet server的master和server上的。

此外，一个 tablet server 既可以成为tablets的leader，也可以成为一个别的follower





















