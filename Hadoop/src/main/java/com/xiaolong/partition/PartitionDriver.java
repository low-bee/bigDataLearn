package com.xiaolong.partition;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PartitionDriver {

    public static void main(String[] args) throws Exception {

        args = new String[]{"d:/test/input","d:/test/output1"};

        Job job = Job.getInstance(new Configuration());
        job.setJarByClass(PartitionDriver.class);
        job.setReducerClass(PartitionReducer.class);
        job.setMapperClass(PartitionMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(PartitionBean.class);
        job.setOutputKeyClass(PartitionBean.class);
        job.setOutputValueClass(Text.class);

        job.setNumReduceTasks(5);
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        boolean b = job.waitForCompletion(true);
        System.exit(b ? 0 : 1);



    }
}
