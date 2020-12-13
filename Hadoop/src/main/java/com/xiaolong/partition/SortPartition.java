package com.xiaolong.partition;

import com.xiaolong.flow.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class SortPartition extends Partitioner<Text, FlowBean> {

    @Override
    public int getPartition(Text text, FlowBean flowBean, int numPartitions) {
        // return (key.hashCode() & Integer.MAX_VALUE) % numReduceTasks; HashPartition返回一个哈希值

        String phone = text.toString();
        switch (phone.substring(0,3)) {
            case "136":
                return 0;
            case "137":
                return 1;
            case "138":
                return 2;
            case "139":
                return 3;
            default:
                return 4;
        }
    }
}
