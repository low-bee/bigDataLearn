# hadoop 学习



## 常用命令

mr-jobhistory-daemon.sh stop historyserver: 停止历史服务器

yarn-daemon.sh stop nodemanager : 停止节点管理

yarn-daemon.sh stop resourcemanager: 停止资源管理器 



## HDFS定义

HDFS(Hadoop Distnbuted File System), 他是一个分布式的文件系统,适合用来存储一次写入,多次读出的文件.并且不支持文件的修改操作

### 优点

高容错性

* 自动保存多个副本, 提高容错性
* 丢失副本后,可以自动恢复

可以构建在廉价的集群上,通过多副本机制,提高可靠性

适合处理大数据

* 数据的规模大
* 文件的规模大

### 缺点

不适合需要低延迟访问的数据

不能高效的存储小文件(namenode撑不住,小文件的寻址时间会超过读取的时间, 违反了HDFS的设计目标)

不支持文件的并发写入和文件的随即修改

* 一个文件只能有一个线程写入
* 仅仅支持数据的追加

HDFS架构

* NameNode,Hadoop的核心,负责管理命名空间, 配置副本策略, 管理数据块映射信息, 处理客户端的读写请求
* DataNode, 是Slave.NameNode下达命令,DataNode负责处理. 它存储着实际的数据块并且执行读写操作

Client: 客户端

* 文件切分, 文件上传时,客户端负责将文件切分成一个一个的block,然后进行上传
* 与NameNode交互
* 与DataNode交互,读取或者写入数据
* Client提供一些命令来管理HDFS
* Client提供一些命令来访问HDFS,例如增删改查

SecondaryNameNode(当NameNode挂掉,并不能马上顶上)

* 协助NameNodd处理工作, 是NameNode的"助理", 定期合并Fsimage和Edits
* 紧急情况下,可以辅助恢复NameNode