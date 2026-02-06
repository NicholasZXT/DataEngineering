package FlinkDemos.c4_partition;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.api.java.functions.KeySelector;
import FlinkDemos.beans.WaterSensor;

/**
 * 分区操作
 */
public class PartitionOperation {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // 从集合中创建源数据
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
            new WaterSensor("s1", 1.0, 1),
            new WaterSensor("s2", 11.0, 2),
            new WaterSensor("s3", 2.0, 3),
            new WaterSensor("s4", 3.0, 4)
        );

        // shuffle随机分区
        sensorDS.shuffle().print("Shuffle");

        // rebalance轮询
        sensorDS.rebalance().print("Rebalance");

        // rescale缩放
        sensorDS.rescale().print("Rescale");

        // broadcast广播
        sensorDS.broadcast().print("BroadCast");

        // global全局： 全部发往 第一个子任务
        sensorDS.global().print("Global");

        // 自定义分区器，第一个是分区器，第二个是key的选择器
        sensorDS.partitionCustom(new MyPartitioner(), new MyKeySelector()).print("CustomPartitioner");

        env.execute();
    }

    // 分区器必须实现 Partitioner 接口
    static class MyPartitioner implements Partitioner<Integer> {
        @Override
        public int partition(Integer key, int numPartitions) {
            return key % numPartitions;
        }
    }

    static class MyKeySelector implements KeySelector<WaterSensor, Integer> {
        @Override
        public Integer getKey(WaterSensor waterSensor) throws Exception {
            // 使用ID作为分区的key
            return waterSensor.getVc();
        }
    }
}


