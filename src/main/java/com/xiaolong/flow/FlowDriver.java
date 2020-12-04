package com.xiaolong.flow;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class FlowDriver {
    public static void main(String[] args) throws Exception {
        args = new String[]{"d:/test/input","d:/test/output1"};

        // 创建一个Job对象
        Job job = Job.getInstance(new Configuration());
        // 为job设置当前输入
        job.setJarByClass(FlowDriver.class);
        // 设置Mapper和Reduce类
        job.setMapperClass(FlowMapper.class);
        job.setReducerClass(FlowReduce.class);
        // 设置Mapper输入和输出的类型
        job.setMapOutputValueClass(FlowBean.class);
        job.setMapOutputKeyClass(Text.class);
        // 设置Reduce输入和输出类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);

    }


}
