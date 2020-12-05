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