package com.xiaolong.learnHDFS;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;


public class HelloWorld {

    private FileSystem fs;

    @Before
    public void creatFS() throws Exception {
        Configuration configuration = new Configuration();
        fs = FileSystem.get(new URI("hdfs://hadoop101:9000"), configuration, "xiaolong");
        System.out.println("创建fs完成");
    }

    @Test
    public void testHelloWorld() throws Exception {
        Configuration configuration = new Configuration();

        FileSystem fs = FileSystem.get(new URI("hdfs://hadoop101:9000"), configuration, "xiaolong");
        fs.delete(new Path("/xiaolong"), true);
        fs.close();
    }

    @After
    public void closeFS() throws Exception {
        fs.close();
        System.out.println("关闭fs成功");
    }

}
