package FlinkDemos;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FlinkDemos.beans.WaterSensor;

/**
 * 合流操作
 */
public class C6_StreamMerge {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // 有两种类型的合流方式：Union 和 Connect
        // Union 要求两个流的元素数据类型必须是一样的，而 Connect 不需要

        // Union合流
        DataStreamSource<Integer> s1 = env.fromElements(1, 2, 3);
        DataStreamSource<Integer> s2 = env.fromElements(11, 22, 33);
        DataStreamSource<String> s3 = env.fromElements("111", "222", "333");
        // 一次可以合并多条流
        DataStream<Integer> unionStream = s1.union(s2, s3.map(Integer::valueOf));
        unionStream.print("UnionStream");

        // ------------------------------------------------------------------------------
        // Connect 合流
        DataStreamSource<WaterSensor> source1 = env.fromElements(
                new WaterSensor("s1", 1.0, 1),
                new WaterSensor("s2", 11.0, 2),
                new WaterSensor("s3", 2.0, 3)
        );
        DataStreamSource<Integer> source2 = env.fromElements(4, 5, 6);
        // 使用 connect 方法合流，一次只能合并两个流ConnectedStreams的泛型参数分别是第1个和第2个流的元素类型
        ConnectedStreams<WaterSensor, Integer> connectedStreams = source1.connect(source2);
        // 合流之后，还需要做一次合并数据类型的操作，这里使用 CoMapFunction 接口来描述此操作
        SingleOutputStreamOperator<WaterSensor> result = connectedStreams.map(new CoMapFunction<WaterSensor, Integer, WaterSensor>() {
            //map1 方法处理第1个流的元素
            @Override
            public WaterSensor map1(WaterSensor value) throws Exception {
                // 第一个流的元素直接返回就行
                return value;
            }
            //map2 方法处理第2个流的元素
            @Override
            public WaterSensor map2(Integer value) throws Exception {
                // 第2个流的元素做一下封装
                return new WaterSensor("s4", 4.0, value);
            }
        });
        result.print("ConnectedStream");

        // --------------------------------------------------------------------------------------------
        // Connect 还可以根据 key 来合流，实现类似于 Inner Join 的操作
        DataStreamSource<Tuple2<String, Integer>> source3 = env.fromElements(
                Tuple2.of("s1", 3),
                Tuple2.of("s2", 4),
                Tuple2.of("s3", 5)
        );
        ConnectedStreams<WaterSensor, Tuple2<String, Integer>> connect = source1.connect(source3);
        // 合流之后，调用 KeyBy 分别从两个流里拿到key
        ConnectedStreams<WaterSensor, Tuple2<String, Integer>> connectKeyed = connect.keyBy(WaterSensor::getId, cs2 -> cs2.f0);
        // 分别按 Key 处理两个流的元素，汇总到一起
        SingleOutputStreamOperator<WaterSensor> connectKeyedResult = connectKeyed.process(new MyKeyedConnectCoProcessFunction());
        connectKeyedResult.print("ConnectKeyedStream");

        env.execute();
    }
}

// TODO
class MyKeyedConnectCoProcessFunction extends CoProcessFunction<WaterSensor, Tuple2<String, Integer>, WaterSensor> {
    // 每条流定义一个hashmap，用来存数据
    Map<String, List<WaterSensor>> s1Cache = new HashMap<>();
    Map<String, List<Tuple2<String, Integer>>> s2Cache = new HashMap<>();

    /**
     * 第一条流的处理逻辑
     * @param value 第一条流的数据
     * @param ctx   上下文
     * @param out   采集器
     * @throws Exception
     */
    @Override
    public void processElement1(WaterSensor value, Context ctx, Collector<WaterSensor> out) throws Exception {

    }

    /**
     * 第二条流的处理逻辑
     * @param value 第二条流的数据
     * @param ctx   上下文
     * @param out   采集器
     * @throws Exception
     */
    @Override
    public void processElement2(Tuple2<String, Integer> value, Context ctx, Collector<WaterSensor> out) throws Exception {

    }
}
