package com.xiaolong.sort;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class SortReducer extends Reducer<SortFlowBean, Text, Text, SortFlowBean> {
    @Override
    protected void reduce(SortFlowBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        for (Text text : values) {
            context.write(text, key);
        }
    }
}
