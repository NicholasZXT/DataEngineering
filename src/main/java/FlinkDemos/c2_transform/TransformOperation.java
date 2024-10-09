package FlinkDemos.c2_transform;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import FlinkDemos.beans.WaterSensor;

/**
 * 演示Flink基本的Transform算子使用
 */
public class TransformOperation {
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

        // -----------------------------------------------------------------------------------------------
        // 使用 map 算子提取其中的 id 字段：返回流的泛型可能会变化
        // 方法1：使用lambda表达式
        //SingleOutputStreamOperator<String> mapStream1 = sensorDS.map(waterSensor -> waterSensor.getId());
        // 不过对于特殊的 POJO 类，lambda表达式可以使用 方法引用 更简洁
        SingleOutputStreamOperator<String> mapStream1 = sensorDS.map(WaterSensor::getId);
        // 方法2：使用匿名实现类
        SingleOutputStreamOperator<String> mapStream2 = sensorDS.map(new MapFunction<WaterSensor, String>() {
            @Override
            public String map(WaterSensor waterSensor) throws Exception {
                return waterSensor.getId();
            }
        });
        // 方法3：定义一个继承了上面 MapFunction 接口的实现类——这个方法比较麻烦，适用于业务逻辑比较复杂的时候
        SingleOutputStreamOperator<String> mapStream3 = sensorDS.map(new MyMapFunc());

        // -----------------------------------------------------------------------------------------------
        // 使用 filter 算子提取其中的  vc > 10 的记录：返回流的泛型 不会 变化
        // 同样也有3种方式
        SingleOutputStreamOperator<WaterSensor> filterStream1 = sensorDS.filter(waterSensor -> waterSensor.getVc() > 10);
        SingleOutputStreamOperator<WaterSensor> filterStream2 = sensorDS.filter(new FilterFunction<WaterSensor>() {
            @Override
            public boolean filter(WaterSensor waterSensor) throws Exception {
                return waterSensor.getVc() > 10;
            }
        });
        SingleOutputStreamOperator<WaterSensor> filterStream3 = sensorDS.filter(new MyFilterFunc());

        // -----------------------------------------------------------------------------------------------
        // 使用 flatMap 算子，实现一进多出：返回流的泛型可能会变化
        SingleOutputStreamOperator<String> flatStream = sensorDS.flatMap(new FlatMapFunction<WaterSensor, String>() {
            @Override
            public void flatMap(WaterSensor waterSensor, Collector<String> collector) throws Exception {
                // 返回值需要交给 Collector 对象来收集
                if(waterSensor.getId().equals("s1")){
                    // s1 的 记录只输出 vc
                    collector.collect("[s1] id: " + waterSensor.getVc().toString());
                }else if(waterSensor.getId().equals("s2")) {
                    // s2 的记录输出 vc 和 ts
                    collector.collect("[s2] vc: " + waterSensor.getVc().toString());
                    collector.collect("[s2] ts: " + waterSensor.getTs().toString());
                }
                // s3 的记录不输出
            }
        });

        flatStream.print("flatmapStream");

        env.execute();
    }

    public static class MyMapFunc implements MapFunction<WaterSensor,String>{
        @Override
        public String map(WaterSensor value) throws Exception {
            return value.getId();
        }
    }

    public static class MyFilterFunc implements FilterFunction<WaterSensor>{
        @Override
        public boolean filter(WaterSensor waterSensor) throws Exception {
            return waterSensor.getVc() > 10;
        }
    }
}
