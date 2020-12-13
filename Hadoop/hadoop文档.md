# hadoop文档

## 集群配置

### 概述

Apache Hadoop 2.10.1 是在2.x.y下的一条线, 构建在之前的稳定版本2.4.1上

这是几个则个版本特性的综述

* common
  * 使用HTTP代理时身份验证方式被改进了, 在使用HTTP代理时将变得非常有用
  * 新的一个允许直接写入Graphite的metric sink (?)
  * 和HDFS规范相关的操作
* HDFS
  * 支持[posix风格](https://my.oschina.net/u/589241/blog/2876942)的文件系统拓展属性
  * 使用`OfflineImageViewer`类, 现在客户端已经可以使用WebHDFS API查看fsImage了
  * NFS网关得到了大量的改进和许多的bug修复. 运行网关现在已经不需要使用Hadoop的端口映射了 并且网关现在有能力拒绝一个没有权限的连接了
  * SecondaryNameNode, JournalNode和DataNode的UI使用更为现代化的HTML5和javascript重新写了
* yarn
  * yarn REST API 现在支持写和重新定义等操作, 用户能提交和结束应用通过这种REST API
  * 存储在yarn中的被用来存储一般的和特殊的应用信息的时间线现在支持通过Kerberios进行身份检测了
  * Fait时间表现在支持动态的分层使用者队列, 使用者队列在运行时在任何指定的父队列中被动态的创建出来

### 让我们开始吧

这个Hadoop文档包含了所有你需要去开始使用Hadoop的信息. 先从单节点的设置开始, 这展示了如何去设置一个单节点的hadoop安装方式.你也可以看[cluster setup](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/ClusterSetup.html)来设置一个多节点的Hadoop



### Hadoop: 设置一个单节点的集群

#### 意图

这个文档的主要意图是让你学会如何去设置和配置一个单节点的hadoop安装程序, 以便于你能很快的开始测试Hadoop MapReduce程序和Hadoop Distributed File System (HDFS)

#### 你需要准备些什么东西

##### 一个被支持的平台

* GNU/Linux 作为一个开发者者的开发和生成平台而被支持. Hadoop已经有运行在GNU/Linux等平台上的拥有2000多个节点的集群
* Windows也被支持. 但是接下来的步骤只包含了Linux中的, 需要的话你可以自行百度.

#### 需要准备好软件

1. Java必须被安装好. 需要看Java版本对照的话, 可以看  [HadoopJavaVersions](http://wiki.apache.org/hadoop/HadoopJavaVersions)
2. ssh必须被安装, 并且sshd必须运行使用hadoop scripts 去管理远程的Hadoop进程

#### 安装软件

如果你的集群中没有这些软件, 你可以通过以下方式获取

类如 linux Ubuntu

```shell
sudo apt-get install ssh
sudo apt-get install rsync
```

#### 下载Hadoop

为了得到一个分布式的文件系统, 下载一个近期的稳定发布版本从[Apache Download Mirrors](http://www.apache.org/dyn/closer.cgi/hadoop/common/)

#### 准备开始配置Hadoop集群

解压缩Hadoop分布式系统, 在此分布式系统中编辑*etc/hadoop/hadoop-env.sh*文件,定义下面的一些参数

```shell
# java安装目录
export JAVA_HOME=你的java安装目录名
```

然后使用下面的命令

`bin/hadoop`

这个命令将展示hadoop的使用文档, 现在你已经准备好使用以下三种方式之一去安装你的集群了

- [Local (Standalone) Mode](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/SingleCluster.html#Standalone_Operation)
- [Pseudo-Distributed Mode](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/SingleCluster.html#Pseudo-Distributed_Operation)
- [Fully-Distributed Mode](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/SingleCluster.html#Fully-Distributed_Operation)

#### 单节点操作

在默认情况下, hadoop被配置为不使用分布式运行的模式, 作为一个Java进程来使用. 这对于debugging是很有用的

接下来的这个案例复制了conf文件目录作为一个输出, 然后其目的是为了找到和展示所有的匹配当前正则表达式的所有文本. output是写出的输出目录

```shell
mkdir input
cp etc/hadoop/*.xml input
bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.10.1.jar grep input output 'dfs[a-z.]+'
cat output/*
```

### 伪分布式系统

hadoop也能作为一个伪分布式系统运行, 在这种情况下,hadoop进程做为一个Java进程来运行

#### configuration

配置以下两个文件 *etc/hadoop/core-site.xml*,  *etc/hadoop/hdfs-site.xml*

```shell
# etc/hadoop/core-site.xml
# 配置NameNode的位置
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

```shell
# etc/hadoop/hdfs-site.xml
# 配置副本数
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
</configuration>
```

#### 配置ssh

```shell
ssh locahosts
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys
```



#### 开始

1. 格式化文件系统

   ```powershell
   hdfs namenode -format
   ```

2. 开启NameNode和DataNode节点

   ```shell
   start-dfs.sh
   ```

3. 浏览默认的web接口,查看HDFS信息, 默认端口如下

   ```
   NameNode - http://localhost:50070/
   ```

4. 用HDFS系统去执行MapReduce job

   ```shell
   bin/hdfs dfs -mkdir /user
   bin/hdfs dfs -mkdir /user/<username>
   ```

5. 复制输出文件到分布式文件系统中

   ```shell
   bin/hdfs dfs -put etc/hadoop input
   ```

6. 运行提供的Hadoop案例

   ```shell
   bin/hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.10.1.jar grep input output 'dfs[a-z.]+'
   ```

7. 检测输出文件: 将文件从HDFS中下载到本地, 然后检测, 或者直接执行cat命令

   ```shell
   bin/hdfs dfs -get output output
   cat output/
   ```

   or

   ```shell
   bin/hdfs dfs -cat output/*
   ```

   

8. 当不需要使用hadoop时, 你可以使用以下命令停止

   ```shell
   sbin/stop-dfs.sh
   ```


### 在单节点使用YARN

你也能使用在伪分布式模式中使用YARN运行MapReduce程序而仅仅只是设置很少的几个参数并且额外运行ResourceManager和NodeManager进程

1. 配置接下来两个文件中的参数*etc/hadoop/mapred-site.xml*, *etc/hadoop/yarn-site.xml*.

   ```shell
   # 配置运行Hadoop的运行框架
   <configuration>
       <property>
           <name>mapreduce.framework.name</name>
           <value>yarn</value>
       </property>
   </configuration>
   ```

   ```shell
   <configuration>
       <property>
           <name>yarn.nodemanager.aux-services</name>
           <value>mapreduce_shuffle</value>
       </property>
   </configuration>
   ```

2. 开始ResourceManager 和NodeManager 进程

   ```shell
   sbin/start-yarn.sh
   ```

3. 浏览ResourceManager的Web 接口, 默认位置是

   ```
   http://localhost:8088/
   ```

4. 运行MapReduce程序

5. 当不再需要使用Yarn时, 你可以通过一下命令停止

   ```shell
   sbin/stop-yarn.sh
   ```



## 完全分布式系统配置

### 意图

当前章节主要介绍怎样安装和配置hadoop运行在几个节点到数千节点的集群. 为了开始使用hadoop, 你可能想要先进行单节点的安装?(see  [Single Node Setup](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/SingleCluster.html)), 这个文档没有覆盖到hadoop安全和高可用方面的内容

### 前期准备

* 安装合适版本的Java, 你可以看 [Hadoop Wiki](http://wiki.apache.org/hadoop/HadoopJavaVersions) 来寻找一个合适的Java版本
* 从apache镜像源中下载一个稳定的hadoop版本

### 开始安装

为了安装一个典型的hadoop集群, 你需要解包这个软件在所有的你需要安装的机器上和一个合适的位置. 对硬件划分出不同的功能区是十分重要的.

典型的一个在集群中的机器其中一个被设计为Namenode另外的一个被设计为ResourceManager, 这是一个主线. 其他的类似于 Web app Proxy Server 或者 MapReduce Job History Server 等通常运行在 专用硬件或者共享基础设置上运行, 这取决于当前集群的负载

集群中的其他机器同时充当DataNode和NodeManager. 这些都是从节点

### 配置Hadoop在非安全的模式中

Hadoop的Java配置由两种重要的配置文件来驱动, 分别是

* 只读的配置: ``core-default.xml`, `hdfs-default.xml`, `yarn-default.xml` and `mapred-default.xml``
* 个人特殊的配置: `etc/hadoop/core-site.xml`, `etc/hadoop/hdfs-site.xml`, `etc/hadoop/yarn-site.xml` and `etc/hadoop/mapred-site.xml`

除此之外, 你也能在bin/目录下运行一些hadoop shell命令

为了配置hadoop集群首先你需要配置environment, 用来确定danghadoop进程运行时,哪些 configuration parameter作为参数

HDFS的守护进程是 NameNode, SecondaryNameNode, 和DataNode. Yarn的守护进程是 ResourceManager, NodeManager和WebAppProxy. 如果MapReduce被使用到了, 那么MapReduce Job History Server 也将运行. 对于大集群安装, 这些大多运行在不同的主机上.

#### 配置Hadoop进程环境

管理者应该使用 etc/hadoop/hadoop-env.sh 和可选的*etc/hadoop/mapred-env.sh*和 *etc/hadoop/yarn-env.sh*命令配置一个自定义的hadoop运行时的进程参数.

**最起码的一点, 你应该定制好JAVA_HOME参数以便于在每一个远程主机上都能能被找到**

管理者也能使用下面表中的可选参数来定制个人的守护进程

| Daemon                        | Environment Variable          |
| :---------------------------- | :---------------------------- |
| NameNode                      | HADOOP_NAMENODE_OPTS          |
| DataNode                      | HADOOP_DATANODE_OPTS          |
| Secondary NameNode            | HADOOP_SECONDARYNAMENODE_OPTS |
| ResourceManager               | YARN_RESOURCEMANAGER_OPTS     |
| NodeManager                   | YARN_NODEMANAGER_OPTS         |
| WebAppProxy                   | YARN_PROXYSERVER_OPTS         |
| Map Reduce Job History Server | HADOOP_JOB_HISTORYSERVER_OPTS |

例如, 要配置一个parallelGC(java的垃圾回收机制), 下面的状态应该被增加在hadoop-env.sh中

```shell
export HADOOP_NAMENODE_OPTS="-XX:+UseParallelGC"
```

### 配置hadoop进程参数

几个核心配置



选择性的处理接下来的配置文件

几个核心文件中的配置

可以去[Hadoop configuration](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/ClusterSetup.html#Configuring_the_Hadoop_Daemons)查看配置

* `etc/hadoop/core-site.xml`

  * fs.defaultFS: 是NameNode 的URI, 访问这个节点就可以访问到集群中的NameNode
  * io.file.buffer.size: 设置序列文件中的读或者写的文件的大小, 默认是131024(16md)

* `etc/hadoop/hdfs-site.xml`

  Configurations for NameNode:

  * dfs.namenode.name.dir: NameNode存储被保存在在本地文件系统的命名空间和事务日志的本地文件目录, 如果是一个逗号隔开的列表, 为了冗余, 会保存多个副本
  * dfs.hosts` / `dfs.hosts.exclude: 有必要的话, 使用这两个文件来控制哪些DataNode能连上集群
  * dfs.blocksize: 默认值是256mb为一个block
  * dfs.namenode.handler.count:  配置处理NameNode服务的线程数

  Configurations for DataNode:

  * dfs.datanode.data.dir: 一个用来存储block的逗号分割的列表

* `etc/hadoop/yarn-site.xml`

  Configurations for ResourceManager and NodeManager:

  * yarn.acl.enable: 配置ACL, 默认为flase
  * yarn.admin.acl: ALC 用于设置集群的管理员, 默认是* 表示所有人都可以访问, 空格表示所有人都不可以访问
  * yarn.log-aggregation-enable: 配置是否启用日志聚合

  Configurations for ResourceManager:

  * yarn.resourcemanager.address: `ResourceManager` host:port for clients to submit jobs.
  * yarn.resourcemanager.scheduler.address
  * ......

  ### 使用NodeManagers监控健康状态

  管理人员可以通过Hadoop提供的监控机制周期行的来判断是否一个节点处于健康状态.

  管理者能够通过任何的他们在这个脚本中看到的健康状态的表现来决定一个节点的状态. 如果这个脚本检测到节点处于不健康的状态, 它就必须输出一个ERROR到标准输出上. NodeManager定期的产生这个脚本并且产生输出.如果脚本的输出包含字符串ERROR.这种情况下, 这个节点将被上报为一个不健康的节点. 并且节点将会被ResourceManager列入黑名单. 没有任务会被这个节点执行. 尽管这样, NodeManager还是会继续运行脚本, 直到这个节点再次变为健康的节点, 它会被自动地移出黑名单之中. 节点的健康状况取决于脚本的输出结果, 如果一个节点被标记为不健康, 那么在ResourceManager的Web接口中对管理员可用. 健康的节点也会展示在这个web 接口中.

  下面的参数被用来监控节点的健康状态

| Parameter                                           | Value                               | Notes                                                 |
| :-------------------------------------------------- | :---------------------------------- | :---------------------------------------------------- |
| `yarn.nodemanager.health-checker.script.path`       | Node health script                  | Script to check for node’s health status.             |
| `yarn.nodemanager.health-checker.script.opts`       | Node health script options          | Options for script to check for node’s health status. |
| `yarn.nodemanager.health-checker.interval-ms`       | Node health script interval         | Time interval for running health script.              |
| `yarn.nodemanager.health-checker.script.timeout-ms` | Node health script timeout interval | Timeout for health script execution.                  |

当本地硬盘损坏了, 健康检查脚本不会报告抛出错误.NodeManager有能力可以周期性的检查本地硬盘 (specifically checks nodemanager-local-dirs and nodemanager-log-dirs). 当检查出损坏的数量到达设置的最小健康硬盘数, 此时所有的节点都会被标为不健康.



### 集群的开启和关闭

[hadoop](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/ClusterSetup.html#Hadoop_Startup)的开启

[hadoop](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/ClusterSetup.html#Hadoop_Shutdown)的关闭

### Web 接口

NameNode: 默认端口 50070

ResourceManager: 默认端口8088

MapReduce JobHistory Server: 默认端口是19888



## hadoop版本兼容性

### 意图

本文档意图捕获hadoop项目的兼容性问题. 不同版本的hadoop发行版影响着开发者, 下游项目,  和不胜枚举的用户. 对于每一种的兼容性问题:

* 我们描述了对下游项目和用户的影响
* 在适用的情况下, 指出当不兼容情况发生时的hadoop开发者策略

### 不兼容类型

#### Java API

hadoop 接口和类被声明用以描述目标受众和稳定的能力为了去兼容之前发布的版本

* InterfaceStability:描述那种类型的接口的改变是被允许的. 可能的值是`Stable`, `Evolving `和 `Deprecate`

使用案例

* public-Stable API兼容性要求确保用户程序和下游项目没有改变的继续工作
* `LimitedPrivate-Stable API`兼容性要求允许更新发布在镜像上的私有组件
* `Private-Stable API`兼容性要求滚动升级

策略

* `Public-Stable APIs` 必须至少在一个稳定的版本中被弃用然后才能在下一个版本中弃用它
* `LimitedPrivate-Stable APIs`可以跨主要版本更改, 但是不允许在主要版本中更改
* `Private-Stable APIs` 可以跨主要版本更改, 但是不允许在主要版本中更改
* 没有被声明的类默认是`Private`. 类成员没有声明表示从上一个封闭的类中继承的
* 从原始文件中生成的API需要滚动升级兼容, 可以看有线兼容部分得到更多的信息. api和有线通信的兼容性策略需要同时解决这个问题。

#### 语义兼容性

Apache hadoop 努力的确保不同版本的API行为的不变, 尽管这个改变的行为是正确的. 测试和java文档指定了当前API的功能和行为.  当前的社区正在更加严格的指定(specifying)一些API, 并且增强测试使得API能正确的服从说明. 努力的创造一个符合规范的容易测试的行为子集

##### 政策

API可能被改变为了修复一个不正确的行为, 例如为了更新一个已经存在的bug测试或者添加一个在没有测试的时期增加的API的测试

#### 有线传输的兼容性

有线传输的兼容性关心的是在两个Hadoop进程之间传输的数据. Hadoop对于绝大多数的RPC(RPC就是要像调用本地的函数一样去调远程函数)通信使用协议缓冲区.保留兼容性需要禁止如下所示的修改. 空的RPC交流也应该被考虑到, 例如, 使用HTTP去发送一个作为一个快照的一部分的HDFS image或者转运MapTask的输出等. 这些潜在的交流能被分为下面的几类:

##### use case

* Client-Server: 在Hadoop Client和Servers之前进行通信(例如HDFS客户端到NameNode的协议或者是Yarn客户端到ResourceManager之间的协议等)
* Client-Server(admin): 对于单独的区分一个使用管理员命令的Client-Server对于那些管理员可以容忍的操作但是用户不能容忍的操作的协议的子集是有价值的(例如HAAdmin 协议)

* Server-Server: 发生在两个服务器之间的通信协议(例如DataNode和NameNode或者是NodeManager和ResourceManager)

##### policy

* 在Client-Server和Server-Server两者中的兼容性是隐藏在一个大的发布版本中(不同的分类有将出现不同的经过深思熟虑的政策)

* 兼容性被打破只有在一个主要的发布版本之间, 即使在主要版本中打破兼容性也会带来巨大的后果, 这应该在社区中被讨论
* Hadoop协议被定义在.proto(protocolBuffers)文件中, Client-Server和Server-Server协议的.proto文件被标记为一个稳定版, 当一个一个.proto文件被标记为稳定版这意味着以下的兼容性方式应该被应用
  * 接下来的改变兼容性能被允许在任何时间
    * 增加一个可选域, 期望能处理和旧版本的通信丢失的字段
    * 给Service增加一个新的远程调用方法或者本地方法
    * 向消息队列添加一个可选的请求
    * 重新命名一个字段
    * 重新命名.proto文件
    * 改变.proto文件的影响代码的范围. 例如java包的名字
  * 接下来的改变是不兼容的但是能被支持在一个主要发行版中
    * 改变RPC/方法的名字
    * 改变RPC/方法的参数类型或者返回值类型
    * 删除一个RPC/方法
    * 改变服务的名称
    * 以不兼容的方式重新定义一个字段
    * 改变请求的可选择字段
    * 增加或者删除一个请求字段
    * 删除可选字段，只要该可选字段具有允许删除的合理默认值
  * 接下来改变是不兼容也不被允许的
    * 改变字段的id
    * 再次使用一个之前被删除的一个老的字段
    * 字段号很简单并且对于改变和再次重用都不是一个好的方法

### Java二进制文件对于终端使用者的兼容性

随着Apache Hadoop的持续更新和迭代, 用户有理由期待他们的应用不需要任何的改变就可以工作. 可以很愉快的兼容API, 语义兼容和有线兼容. 

然而, Apache Hadoop 是一个非常负责的分布式的系统. 并且其用户的服务非常的多变. 在这种情况下. Apache Hadoop MapReduce 是一个非常非常宽泛的API. 从这个意义上说, 用户可能做出非常个性化的配置,例如当他们的MapReduce任务运行的时候的硬盘布局, 环境变量等. 在这种情况下, 满足绝对的兼容性和支持变成一件非常困难的事.

##### Use cases

* 存在许多的MapReduce应用, 包括已经存在的终端用户的jar包和项目例如Apache pig , Apache hive, Cascading 等. 当Hadoop更新之后应该不需要任何的改变就可以在集群中运行
* 当更新一个主要的发行版之后已经存在的YARN 应用包括已经存在的项目和jar包应该不需要修改代码就能够运行
* 当更新一个主要的发行版,一些在HDFS中转运数据的输入和输出的应用, 包括已经存在的jar包和使用者的应用和框架应该不需要修改就能工作

##### policy

* 存在一个MapReduce, Yarn & HDFS应用框架在一个主要的发行版应该不变
* 有很小的一部分可能因为硬盘布局等因素被影响到的应用, 开发者社区将最小化这种改变并且将使这些改变在一个很小的版本中.在更加臭名昭著的案例中, 如果必要的话, 我们将考虑强烈的要求恢复更改的请求并且使发布版无效
* 在一些特定的MapReduce应用中, 开发者社区将尝试提供最好的跨越主要版本的兼容的二进制文件
* API在1.x和2.x之间的兼容性得到了支持

MY ENGLISH IS POOR

