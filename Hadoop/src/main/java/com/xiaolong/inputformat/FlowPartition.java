package com.xiaolong.inputformat;

import com.xiaolong.flow.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class FlowPartition extends Partitioner<Text, FlowBean> {

    public int getPartition(Text text, FlowBean flowBean, int numPartitions) {
        String phone = text.toString();
        if ("136".contentEquals(phone.subSequence(0, 3))) {
            return 0;
        } else if ("137".contentEquals(phone.subSequence(0, 3))) {
            return 1;
        } else if ("138".contentEquals(phone.subSequence(0, 3))) {
            return 2;
        } else if ("139".contentEquals(phone.subSequence(0, 3))) {
            return 3;
        }
        return 4;
    }
}
