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

选择性的处理接下来的配置文件, 

太多了, 具体请看 [hadoop配置](https://hadoop.apache.org/docs/r2.10.1/hadoop-project-dist/hadoop-common/ClusterSetup.html#Configuring_the_Hadoop_Daemons)

