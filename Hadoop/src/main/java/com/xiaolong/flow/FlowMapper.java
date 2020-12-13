package com.xiaolong.flow;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

    private final Text phone = new Text();
    private final FlowBean flowBean = new FlowBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] data = value.toString().split("\t");

        phone.set(data[1]);
        flowBean.setUpFlow(Long.parseLong(data[data.length - 3]));
        flowBean.setDownFlow(Long.parseLong(data[data.length - 2]));

        context.write(phone, flowBean);

    }
}
