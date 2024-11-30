package FlinkDemos.c3_aggregate;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.common.functions.ReduceFunction;

import FlinkDemos.beans.WaterSensor;

/**
 * 演示Flink基本聚合算子使用
 */
public class AggregateOperation {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从集合中创建源数据
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
            new WaterSensor("s1", 1.0, 1),
            new WaterSensor("s1", 11.0, 11),
            new WaterSensor("s2", 2.0, 2),
            new WaterSensor("s3", 3.0, 3)
        );

        // ------------------------------------------------------------------------------------------------
        // 聚合之前必须要先做分区（分组）操作
        // 先分组，返回的是 一个 KeyedStream，注意，keyBy不是 转换算子，只是对数据进行重分区，不能设置并行度
        // 使用 lambda 表达式 + 方法引用
        KeyedStream<WaterSensor, String> sensorKS1 = sensorDS.keyBy(WaterSensor::getId);
        // 使用匿名类
        KeyedStream<WaterSensor, String> sensorKS2 = sensorDS
            .keyBy(new KeySelector<WaterSensor, String>() {
                // KeySelector 有两个泛型参数，第一个是数据对象，第二个指定 key 的数据类型
                @Override
                public String getKey(WaterSensor value) throws Exception {
                    // 使用ID作为分区的key
                    return value.getId();
                }
            });
        // 此时直接打印没啥区别
        //sensorKS1.print("KeyedStream");

        // ------------------------------------------------------------------------------------------------
        // 简单聚合
        SingleOutputStreamOperator<WaterSensor> result1 = sensorKS1.max("vc");
        SingleOutputStreamOperator<WaterSensor> result2 = sensorKS1.min("vc");
        SingleOutputStreamOperator<WaterSensor> result3 = sensorKS1.maxBy("vc");
        SingleOutputStreamOperator<WaterSensor> result4 = sensorKS1.minBy("vc");

        // ------------------------------------------------------------------------------------------------
        // reduce
        SingleOutputStreamOperator<WaterSensor> reduce = sensorKS1.reduce(new ReduceFunction<WaterSensor>() {
            @Override
            public WaterSensor reduce(WaterSensor value1, WaterSensor value2) throws Exception {
                System.out.println("value1=" + value1);
                System.out.println("value2=" + value2);
                return new WaterSensor(value1.id, value2.ts, value1.vc + value2.vc);
            }
        });
        reduce.print("reduce");

        env.execute();

    }
}
