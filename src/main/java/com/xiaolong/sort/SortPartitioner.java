package com.xiaolong.sort;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class SortPartitioner extends Partitioner <SortFlowBean, Text>{
    @Override
    public int getPartition(SortFlowBean sortFlowBean, Text text, int numPartitions) {
        switch (text.toString().substring(0, 3)) {
            case "135":
                return 0;
            case "136":
                return 1;
            case "137":
                return 2;
            default:
                return 3;
        }
    }
}
