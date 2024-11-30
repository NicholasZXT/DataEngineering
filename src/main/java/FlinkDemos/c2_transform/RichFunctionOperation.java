package FlinkDemos.c2_transform;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import FlinkDemos.beans.WaterSensor;

/**
 * 演示富函数的使用
 * 富函数相比于普通的转换算子，可以获取获取运行环境的上下文，并且含有一些生命周期方法，可以进行更加复杂的操作。
 */
public class RichFunctionOperation {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        // 从集合中创建源数据
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
            new WaterSensor("s1", 1.0, 1),
            new WaterSensor("s1", 11.0, 11),
            new WaterSensor("s2", 2.0, 2),
            new WaterSensor("s3", 3.0, 3)
        );

        SingleOutputStreamOperator<Double> map = sensorDS.map(
            // 实现 RichMapFunction接口 的匿名类
            new RichMapFunction<WaterSensor, Double>() {
                // 初始化方法，只在算子开启时执行一次
                @Override
                public void open(Configuration parameters) throws Exception {
                    // 开启方法的参数 Configuration 是Flink执行环境的参数对象
                    super.open(parameters);
                    System.out.println(
                        "子任务编号=" + getRuntimeContext().getIndexOfThisSubtask()
                            + ",子任务名称=" + getRuntimeContext().getTaskNameWithSubtasks()
                            + ",调用open()"
                    );
                }
                // 关闭方法，只在算子关闭时执行一次
                @Override
                public void close() throws Exception {
                    // 关闭方法没有参数
                    super.close();
                    System.out.println(
                        "子任务编号=" + getRuntimeContext().getIndexOfThisSubtask()
                            + ",子任务名称=" + getRuntimeContext().getTaskNameWithSubtasks()
                            + ",调用close()"
                    );
                }
                // map 方法，每条数据执行一次
                @Override
                public Double map(WaterSensor record) throws Exception {
                    return record.getTs();
                }
            }
        );

        map.print("RichMapFunction");
        env.execute();
    }
}
