package com.xiaolong.outputformat;

import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyRecordFormat extends RecordWriter<LongWritable, Text> {

    FileOutputStream atguigu;
    FileOutputStream others;

    public void initialization() throws Exception {
        atguigu = new FileOutputStream("d:\\test\\output\\atguigu.log");
        others = new FileOutputStream("d:\\test\\output\\others.log");
    }


    @Override
    public void write(LongWritable key, Text value) throws IOException, InterruptedException {
        String s = value.toString() + "\r\n";

        if (s.contains("atguigu")) {
            atguigu.write(s.getBytes());
        } else {
            others.write(s.getBytes());
        }
    }

    @Override
    public void close(TaskAttemptContext context) throws IOException, InterruptedException {
        IOUtils.closeStream(atguigu);
        IOUtils.closeStream(others);
    }
}
