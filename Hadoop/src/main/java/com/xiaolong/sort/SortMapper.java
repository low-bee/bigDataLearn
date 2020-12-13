package com.xiaolong.sort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;


public class SortMapper extends Mapper<LongWritable, Text, SortFlowBean, Text> {

    private Text phone = new Text();
    private SortFlowBean sortFlowBean = new SortFlowBean();



    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] fields = value.toString().split("\t");

        phone.set(fields[0]);
        sortFlowBean.setUpFlow(Long.parseLong(fields[1]));
        sortFlowBean.setDownFlow(Long.parseLong(fields[2]));
        sortFlowBean.setSumFlow(Long.parseLong(fields[3]));

        context.write(sortFlowBean, phone);
    }
}
