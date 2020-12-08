package com.xiaolong.partition;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class PartitionMapper extends Mapper<LongWritable, Text, PartitionBean, Text> {

    Text phone = new Text();
    PartitionBean partitionBean = new PartitionBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] fields = value.toString().split("\t");

        phone.set(fields[0]);
        partitionBean.setUpFlow(Long.parseLong(fields[1]));
        partitionBean.setDownFlow(Long.parseLong(fields[2]));
        partitionBean.setSumFlow(Long.parseLong(fields[3]));

        context.write(partitionBean, phone);
    }
}
