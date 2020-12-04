package com.xiaolong.inputformat;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

public class WholeFileRecordReader extends RecordReader<Text, BytesWritable> {
    private boolean notRead = true;

    private final Text key = new Text();
    private final BytesWritable value = new BytesWritable();

    private FileSplit fs;
    private FSDataInputStream inputStream;

    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        // 转换切片类型到文件切片
        fs = (FileSplit) split;

        // 通过切片获取路径
        Path path = fs.getPath();

        // 通过路径获取文件系统
        FileSystem fileSystem = path.getFileSystem(context.getConfiguration());

        // 开流
        inputStream = fileSystem.open(path);
    }

    public boolean nextKeyValue() throws IOException, InterruptedException {
        if (notRead){
            // 做读取文件操作
            // 读key
            key.set(fs.getPath().toString());

            // 读value
            byte[] bytes = new byte[(int) fs.getLength()];
            int read = inputStream.read(bytes);
            value.set(bytes, 0, bytes.length);

            notRead = false;
            return true;
        } else {

            return false;
        }
    }

    public Text getCurrentKey() throws IOException, InterruptedException {
        return key;
    }

    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    public float getProgress() throws IOException, InterruptedException {
        return notRead ? 0 : 1;
    }

    public void close() throws IOException {
        IOUtils.closeStream(inputStream);
    }
}
