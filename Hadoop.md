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

## 常用命令

```shell
[-appendToFile <localsrc> ... <dst>]
[-cat [-ignoreCrc] <src> ...]
[-checksum <src> ...]
[-chgrp [-R] GROUP PATH...]
[-chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH...]
[-chown [-R] [OWNER][:[GROUP]] PATH...]
[-copyFromLocal [-f] [-p] <localsrc> ... <dst>]
[-copyToLocal [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
[-count [-q] <path> ...]
[-cp [-f] [-p] <src> ... <dst>]
[-createSnapshot <snapshotDir> [<snapshotName>]]
[-deleteSnapshot <snapshotDir> <snapshotName>]
[-df [-h] [<path> ...]]
[-du [-s] [-h] <path> ...]
[-expunge]
[-get [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
[-getfacl [-R] <path>]
[-getmerge [-nl] <src> <localdst>]
[-help [cmd ...]]
[-ls [-d] [-h] [-R] [<path> ...]]
[-mkdir [-p] <path> ...]
[-moveFromLocal <localsrc> ... <dst>]
[-moveToLocal <src> <localdst>]
[-mv <src> ... <dst>]
[-put [-f] [-p] <localsrc> ... <dst>]
[-renameSnapshot <snapshotDir> <oldName> <newName>]
[-rm [-f] [-r|-R] [-skipTrash] <src> ...]
[-rmdir [--ignore-fail-on-non-empty] <dir> ...]
[-setfacl [-R] [{-b|-k} {-m|-x <acl_spec>} <path>]|[--set <acl_spec> <path>]]
[-setrep [-R] [-w] <rep> <path> ...]
[-stat [format] <path> ...]
[-tail [-f] <file>]
[-test -[defsz] <path>]
[-text [-ignoreCrc] <src> ...]
[-touchz <path> ...]
[-usage [cmd ...]]
```

## HDFS客户端操作

1. 根据自己的电脑的操作系统选择合适的版本

2. 配置一个HADOOP_HOME环境变量

3. 配置Path环境变量

4. 创建一个Maven工程

5. 导入相应的模块

   1. ```xml
      <dependencies>
      		<dependency>
      			<groupId>junit</groupId>
      			<artifactId>junit</artifactId>
      			<version>RELEASE</version>
      		</dependency>
      		<dependency>
      			<groupId>org.apache.logging.log4j</groupId>
      			<artifactId>log4j-core</artifactId>
      			<version>2.8.2</version>
      		</dependency>
      		<dependency>
      			<groupId>org.apache.hadoop</groupId>
      			<artifactId>hadoop-common</artifactId>
      			<version>2.7.2</version>
      		</dependency>
      		<dependency>
      			<groupId>org.apache.hadoop</groupId>
      			<artifactId>hadoop-client</artifactId>
      			<version>2.7.2</version>
      		</dependency>
      		<dependency>
      			<groupId>org.apache.hadoop</groupId>
      			<artifactId>hadoop-hdfs</artifactId>
      			<version>2.7.2</version>
      		</dependency>
      		<dependency>
      			<groupId>jdk.tools</groupId>
      			<artifactId>jdk.tools</artifactId>
      			<version>1.8</version>
      			<scope>system</scope>
      			<systemPath>${JAVA_HOME}/lib/tools.jar</systemPath>
      		</dependency>
      </dependencies>
      
      ```

6. 配置log4j

   1. ```properties
      log4j.rootLogger=INFO, stdout
      log4j.appender.stdout=org.apache.log4j.ConsoleAppender
      log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
      log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - %m%n
      log4j.appender.logfile=org.apache.log4j.FileAppender
      log4j.appender.logfile.File=target/spring.log
      log4j.appender.logfile.layout=org.apache.log4j.PatternLayout
      log4j.appender.logfile.layout.ConversionPattern=%d %p [%c] - %m%n
      ```

7. 创建一个包com.xiaolong.hdfs

8. 创建一个测试类 HdfsClient

   1. ```java
      public class HdfsClient{	
      @Test
      public void testMkdirs() throws IOException, InterruptedException, URISyntaxException{
      		
      		// 1 获取文件系统
      		Configuration configuration = new Configuration();
      		// 配置在集群上运行
      		// configuration.set("fs.defaultFS", "hdfs://hadoop102:9000");
      		// FileSystem fs = FileSystem.get(configuration);
      
      		FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:9000"), configuration, "xiaolong");
      		
      		// 2 创建目录
      		fs.mkdirs(new Path("/1108/daxian/banzhang"));
      		
      		// 3 关闭资源
      		fs.close();
      	}
      }
      
      ```

## HDFS的数据流

### HDFS写数据流程

1. 客户端通过Distributed FileSystem模块项NameNode发送上传文件请求, NameNode检查目标文件是否已经存在, 父目录是否存在
2. NameNode返回是否可以上传
3. 客户端请求上传第一个Black上传到那几个DataNode节点上
4. NameNode返回3个DataNode节点, 分别为dn1, dn2 dn3
5. 客户端通过FSDataOutputStream模块请求dn1上传数据, dn1收到请求后会继续掉用dn2,以此类推.将这个通道建立起来
6. 客户端开始向dn1上上传数据, dn1收到一个Packet就会传给dn2,以此类推, dn1每一个packet都会放入一个应答队列等待应答
7. 当一个block上传完成后, 客户端再次请求上传第二个block的服务器,重复步骤3-7

### 数据可靠性

副本的配置对HDFS来说尤为重要. 这是HDFS区别于其他分布式文件系统的一个主要的地方. 支持机架的副本放置策略目的是提高对数据的可靠性,可用性和网络带宽的利用所综合下来的结果.

一个大的HDFS实例通常运行在一个有许多机架的集群中.在两个DataNode节点上两台机器的通信一般需要通过转换器.在大多数的情况下,同一个机架上的两台DataNode的带宽会明显快于不同机架上的两台DataNode.

一个简单但是不怎么好的策略是将三个副本放在三个机架中,这样可以提高可靠性,并且增加读的效率,但是对于写不友好,因为放置在不同的机架上,会导致写操作需要跨越多个机架

一个更常用的策略是放一个副本在本机架的A节点, 再放一个在本机架的B节点,再放一个在另一个机架上的C节点.因为机架的不可靠性较低,因此这种策略没有影响到可靠性和可用性.并且提高了写的效率.因为三个副本在三个节点, 并且其中两份保存在不同的机架上.这种策略也提高了写的效率

这种默认的策略真在当前版本的程序中被用到

> The placement of replicas is critical to HDFS reliability and performance. Optimizing replica placement distinguishes HDFS from most other distributed file systems. This is a feature that needs lots of tuning and experience. The purpose of a rack-aware replica placement policy is to improve data reliability, availability, and network bandwidth utilization. The current implementation for the replica placement policy is a first effort in this direction. The short-term goals of implementing this policy are to validate it on production systems, learn more about its behavior, and build a foundation to test and research more sophisticated policies.
>
> Large HDFS instances run on a cluster of computers that commonly spread across many racks. Communication between two nodes in different racks has to go through switches. In most cases, network bandwidth between machines in the same rack is greater than network bandwidth between machines in different racks.
>
> The NameNode determines the rack id each DataNode belongs to via the process outlined in [Hadoop Rack Awareness](http://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/ClusterSetup.html#HadoopRackAwareness). A simple but non-optimal policy is to place replicas on unique racks. This prevents losing data when an entire rack fails and allows use of bandwidth from multiple racks when reading data. This policy evenly distributes replicas in the cluster which makes it easy to balance load on component failure. However, this policy increases the cost of writes because a write needs to transfer blocks to multiple racks.
>
> For the common case, when the replication factor is three, HDFS’s placement policy is to put one replica on one node in the local rack, another on a different node in the local rack, and the last on a different node in a different rack. This policy cuts the inter-rack write traffic which generally improves write performance. The chance of rack failure is far less than that of node failure; this policy does not impact data reliability and availability guarantees. However, it does reduce the aggregate network bandwidth used when reading data since a block is placed in only two unique racks rather than three. With this policy, the replicas of a file do not evenly distribute across the racks. One third of replicas are on one node, two thirds of replicas are on one rack, and the other third are evenly distributed across the remaining racks. This policy improves write performance without compromising data reliability or read performance.
>
> The current, default replica placement policy described here is a work in progress.



### HDFS读数据流程

1. 客户端通过Distributed FileSystem 向NameNode请求下载文件, NameNode通过查询原始数据, 找到块文件所在的DataNode地址
2. 挑选一台DataNode(根据网络拓扑距离的就近原则, 然后随机挑选)服务器, 请求写入数据
3. DataNode开始传输数据给客户端(从磁盘读取数据, 以Packet为单位做校验)
4. 客户端以Packet为单位接收,先在本地缓存,然后写入目标

## NameNode和SecondaryNameNode



#### 文件系统的持久化

为了应对NameNode持久化问题, NameNode使用了一个叫做EditLog的事务日志来持久化每一次元数据记录的改变. 例如简单的改变副本配置数量等,这些改变都会被忠实的就在EditLog中,并且, EditLog存储在本地. 所有的命名空间包括blocks的映射和文件系统的配置都存储在FsImage文件中. 并且FsImage也作为一个文件存储在本地文件系统中.

并且, NameNode在内存中保存了全部文件系统命名空间和块映射. 并且由于元数据被设计的十分的紧凑,一个4G的内存就已经可以保存很多的文件和目录了.每次NameNode节点开始运行, 它都会去读取硬盘上的FsImage和EditLog文件. 并且将所有的在EditLog中的事务写入到FsImage中.并且重新疆FsImage写入硬盘. 他会废弃老的EditLog文件,因为这个文件已经持久化到FsImage文件中了.这个过程就叫做checkpoint

### 2NN工作机制

  首先,元数据应该存放在内存中, 因为需要高速的响应客户请求和随机访问. 但是存在内存中带来的问题就是会产生断电元数据丢失, 因此需要在磁盘中备份元数据,就产生了FsImage.

 但是这也也会产生新的问题,当内存中的元数据更新,如果此时立即更新FsImage,会导致效率很低,但是如果不更新就会产生一致性问题.因此EditLog孕运而生.EditsLog文件只进行追加操作,效率很高.每当元数据有更新或者添加新的数据时, 修改内存中的元数据并且追加到EditsLog中.这样一旦NameNode断电,也可以通过合并FsImage和Edits文件来恢复

  当然,如果数据长时间添加到Edits中, 会导致Edits文件过大,因此, Edits需要定期和FsImage合并,这个操作由NameNode来做显然不合适,因此引入了一个新的节点, SecondaryNameNode来辅助NameNode做这件事,专门用于FsImage和Edits文件的合并

### 工作流程

#### 第一阶段

1. 第一次启动NameNode格式化之后, Hadoop会创建FsImage文件和Edits文件, 如果不试试第一次启动, 那么就直接加载编辑日志和镜像文件到内存中.
2. 客户端对元数据进行增删改请求.
3. NameNode记录日志操作, 更新滚动日志
4. NameNode在内存中对数据进行增删改

#### 第二阶段

1. SecondaryNameNode 询问NameNode是否需要 CheckPoint, 直接带回NameNode是否检查结果
2. SecondaryNameNode 请求执行CheckPoint
3. NameNode滚工正在写的Edits日志
4. 将滚动前的编辑日志和镜像文件拷贝到SecondaryNameNode中
5. SecondaryNameNode加载编辑日志和镜像文件到内存中,并合并
6. 生成新的镜像文件fsimage.CheckPoint到NameNode
7. NameNode将fsimage.checkpoint重新命名为fsimage

### 工作机制详解

Fsimage：NameNode内存中元数据序列化后形成的文件。

Edits：记录客户端更新元数据信息的每一步操作（可通过Edits运算出元数据）。

NameNode启动时，先滚动Edits并生成一个空的edits.inprogress，然后加载Edits和Fsimage到内存中，此时NameNode内存就持有最新的元数据信息。Client开始对NameNode发送元数据的增删改的请求，这些请求的操作首先会被记录到edits.inprogress中（查询元数据的操作不会被记录在Edits中，因为查询操作不会更改元数据信息），如果此时NameNode挂掉，重启后会从Edits中读取元数据的信息。然后，NameNode会在内存中执行元数据的增删改的操作。

由于Edits中记录的操作会越来越多，Edits文件会越来越大，导致NameNode在启动加载Edits时会很慢，所以需要对Edits和Fsimage进行合并（所谓合并，就是将Edits和Fsimage加载到内存中，照着Edits中的操作一步步执行，最终形成新的Fsimage）。SecondaryNameNode的作用就是帮助NameNode进行Edits和Fsimage的合并工作。

SecondaryNameNode首先会询问NameNode是否需要CheckPoint（触发CheckPoint需要满足两个条件中的任意一个，定时时间到和Edits中数据写满了）。直接带回NameNode是否检查结果。SecondaryNameNode执行CheckPoint操作，首先会让NameNode滚动Edits并生成一个空的edits.inprogress，滚动Edits的目的是给Edits打个标记，以后所有新的操作都写入edits.inprogress，其他未合并的Edits和Fsimage会拷贝到SecondaryNameNode的本地，然后将拷贝的Edits和Fsimage加载到内存中进行合并，生成fsimage.chkpoint，然后将fsimage.chkpoint拷贝给NameNode，重命名为Fsimage后替换掉原来的Fsimage。NameNode在启动时就只需要加载之前未合并的Edits和Fsimage即可，因为合并过的Edits中的元数据信息已经被记录在Fsimage中。