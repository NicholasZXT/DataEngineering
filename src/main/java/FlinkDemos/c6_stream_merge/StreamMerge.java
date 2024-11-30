package FlinkDemos.c6_stream_merge;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FlinkDemos.beans.WaterSensor;

/**
 * 合流操作
 */
public class StreamMerge {
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
        //unionStream.print("UnionStream");

        // ------------------------------------------------------------------------------
        // Connect 合流
        DataStreamSource<WaterSensor> source1 = env.fromElements(
            new WaterSensor("s1", 1.0, 1),
            new WaterSensor("s2", 11.0, 2),
            new WaterSensor("s3", 2.0, 3)
        );
        DataStreamSource<Integer> source2 = env.fromElements(4, 5, 6);
        // 使用 connect 方法合流，一次只能合并两个流，ConnectedStreams的泛型参数分别是 第1个流 和 第2个流 的元素类型
        ConnectedStreams<WaterSensor, Integer> connectedStreams = source1.connect(source2);
        // 合流之后，还需要做一次合并数据类型的操作，这里使用了 map 方法 + CoMapFunction 接口
        SingleOutputStreamOperator<WaterSensor> result = connectedStreams.map(
            // CoMapFunction 接口的3个泛型参数依次是：第1条流元素，第2条流元素，合并后的流元素
            new CoMapFunction<WaterSensor, Integer, WaterSensor>() {
                //map1 方法处理第1个流的元素
                @Override
                public WaterSensor map1(WaterSensor value) throws Exception {
                    // 第一个流的元素直接返回就行
                    return value;
                }
                //map2 方法处理第2个流的元素
                @Override
                public WaterSensor map2(Integer value) throws Exception {
                    // 第2个流的元素做一下封装，转换成 WaterSensor 类型
                    return new WaterSensor("s4", 4.0, value);
                }
            }
        );
        //result.print("ConnectedStream");

        // --------------------------------------------------------------------------------------------
        // Connect 还可以根据 key 来合流，实现类似于 Inner Join 的操作
        DataStreamSource<Tuple2<Integer, String>> source3 = env.fromElements(
            Tuple2.of(1, "a1"),
            Tuple2.of(1, "a2"),
            Tuple2.of(2, "b"),
            Tuple2.of(3, "c")
        );
        DataStreamSource<Tuple3<Integer, String, Integer>> source4 = env.fromElements(
            Tuple3.of(1, "aa1", 1),
            Tuple3.of(1, "aa2", 2),
            Tuple3.of(2, "bb", 1),
            Tuple3.of(3, "cc", 1)
        );
        ConnectedStreams<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>> connect = source3.connect(source4);
        // 合流之后，调用 KeyBy 分别从两个流里拿到key，之后两个流里相同 key 的元素会被分发到一起
        ConnectedStreams<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>> connectKeyed = connect.keyBy(cs3 -> cs3.f0, cs4 -> cs4.f0);
        // 分别按 Key 处理两个流的元素，汇总到一起
        SingleOutputStreamOperator<String> connectKeyedResult = connectKeyed.process(new MyKeyedConnectCoProcessFunction());
        connectKeyedResult.print("ConnectKeyedStream");

        env.execute();
    }
}

class MyKeyedConnectCoProcessFunction extends CoProcessFunction<Tuple2<Integer, String>, Tuple3<Integer, String, Integer>, String> {
    // 每条流定义一个hashmap，用来存数据
    Map<Integer, List<Tuple2<Integer, String>>> s1Cache = new HashMap<>();
    Map<Integer, List<Tuple3<Integer, String, Integer>>> s2Cache = new HashMap<>();

    /**
     * 第一条流的处理逻辑
     * @param value 第一条流的数据
     * @param ctx   上下文
     * @param out   采集器
     * @throws Exception
     */
    @Override
    public void processElement1(
        Tuple2<Integer, String> value, Context ctx, Collector<String> out
    ) throws Exception {
        Integer id = value.f0;
        // 1. s1的数据来了，就存到变量中
        if (!s1Cache.containsKey(id)) {
            // 1.1 如果key不存在，说明是该key的第一条数据，初始化，put进map中
            List<Tuple2<Integer, String>> s1Values = new ArrayList<>();
            s1Values.add(value);
            s1Cache.put(id, s1Values);
        } else {
            // 1.2 key存在，不是该key的第一条数据，直接添加到 value的list中
            s1Cache.get(id).add(value);
        }
        // 2.去 s2Cache中查找是否有id能匹配上的,匹配上就输出，没有就不输出
        if (s2Cache.containsKey(id)) {
            for (Tuple3<Integer, String, Integer> s2Element : s2Cache.get(id)) {
                out.collect("s1:" + value + "<========>" + "s2:" + s2Element);
            }
        }
    }

    /**
     * 第二条流的处理逻辑
     * @param value 第二条流的数据
     * @param ctx   上下文
     * @param out   采集器
     * @throws Exception
     */
    @Override
    public void processElement2(
        Tuple3<Integer, String, Integer> value, Context ctx, Collector<String> out
    ) throws Exception {
        Integer id = value.f0;
        // 1. s2的数据来了，就存到变量中
        if (!s2Cache.containsKey(id)) {
            // 1.1 如果key不存在，说明是该key的第一条数据，初始化，put进map中
            List<Tuple3<Integer, String, Integer>> s2Values = new ArrayList<>();
            s2Values.add(value);
            s2Cache.put(id, s2Values);
        } else {
            // 1.2 key存在，不是该key的第一条数据，直接添加到 value的list中
            s2Cache.get(id).add(value);
        }
        // 2.去 s1Cache中查找是否有id能匹配上的,匹配上就输出，没有就不输出
        if (s1Cache.containsKey(id)) {
            for (Tuple2<Integer, String> s1Element : s1Cache.get(id)) {
                out.collect("s1:" + s1Element + "<========>" + "s2:" + value);
            }
        }
    }
}
