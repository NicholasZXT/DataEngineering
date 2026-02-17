package FlinkDemos.c9_state;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import FlinkDemos.beans.WaterSensor;
import FlinkDemos.c7_window.TimeWindowAgg;

/**
 * 演示托管状态里，KeyedState 的各个用法
 */
public class KeyedStateUsage {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从集合中创建源数据
        //DataStreamSource<WaterSensor> sensorDS = env.fromElements(
        //    new WaterSensor("s1", 1.0, 1),
        //    new WaterSensor("s2", 2.0, 2),
        //    new WaterSensor("s1", 3.0, 3),
        //    new WaterSensor("s2", 4.0, 4),
        //    new WaterSensor("s1", 5.0, 5),
        //    new WaterSensor("s2", 6.0, 6),
        //    new WaterSensor("s1", 7.0, 7),
        //    new WaterSensor("s2", 8.0, 8)
        //);
        // 这个演示建议使用 socket 流
        SingleOutputStreamOperator<WaterSensor> sensorDS = env
            .socketTextStream("localhost", 7890)
            .map(new TimeWindowAgg.WaterSensorMapFunction())
            .assignTimestampsAndWatermarks(
                WatermarkStrategy
                    .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                    .withTimestampAssigner((element, ts) -> element.getTs().longValue() * 1000L)
            );
        // KeyedState 的前提是必须要有分区
        KeyedStream<WaterSensor, String> sensorKDS = sensorDS.keyBy(WaterSensor::getId);

        // ---------------- ValueState 使用 ----------------
        // 检测每种传感器的水位值，如果连续的两个水位值超过10，就输出报警
        sensorKDS
            .process(new MyValueStateProcFunc())
            .print("KeyedState -> ValueState");
        
        // ---------------- ListState 使用 ----------------
        // 针对每种传感器输出最高的3个水位值
        sensorKDS
            .process(new MyListStateProcFunc())
            .print("KeyedState -> ListState");
        
        // ---------------- MapState 使用 ----------------
        // 统计每种传感器每种水位值出现的次数
        sensorKDS
            .process(new MyMapStateProcFunc())
            .print("KeyedState -> MapState");

        // ---------------- ReducingState 使用 ----------------
        //计算每种传感器的水位总和
        sensorKDS
            .process(new MyReducingStateProcFunc())
            .print("KeyedState -> ReducingState");

        // ---------------- AggregatingState 使用 ----------------
        // 计算每种传感器的平均水位
        sensorKDS
            .process(new MyAggregatingStateProcFunc())
            .print("KeyedState -> AggregatingState");

        env.execute();
    }

    public static class MyValueStateProcFunc extends KeyedProcessFunction<String, WaterSensor, String> {
        // 定义一个状态存储变量
        ValueState<Integer> lastVcState;

        @Override
        public void open (Configuration parameters) throws Exception {
            super.open(parameters);
            // 在 open 方法里，初始化状态
            // 状态描述器两个参数：第一个参数，起个名字，不重复；第二个参数，存储的类型
            lastVcState = getRuntimeContext().getState(
                new ValueStateDescriptor<Integer>("lastVcState", Types.INT)
            );
        }

        @Override
        public void close() throws Exception {
            super.close();
            // close 方法这里用不到，为空即可
        }

        @Override
        public void processElement (
            WaterSensor value, KeyedProcessFunction < String, WaterSensor, String >.Context ctx, Collector<String > out
        ) throws Exception {
            //lastVcState.value();  // 取出 本组 值状态 的数据
            //lastVcState.update(); // 更新 本组 值状态 的数据
            //lastVcState.clear();  // 清除 本组 值状态 的数据
            // 1. 取出上一条数据的水位值(Integer默认值是null，判断)
            int lastVc = lastVcState.value() == null ? 0 : lastVcState.value();
            // 2. 求差值的绝对值，判断是否超过10
            Integer vc = value.getVc();
            if (Math.abs(vc - lastVc) > 10) {
                out.collect("传感器=" + value.getId() + "==>当前水位值=" + vc + ",与上一条水位值=" + lastVc + ",相差超过10！");
            }
            // 3. 更新状态里的水位值
            lastVcState.update(vc);
        }
    }


    public static class MyListStateProcFunc extends KeyedProcessFunction<String, WaterSensor, String> {
        // List状态存储变量
        ListState<Integer> vcListState;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            vcListState = getRuntimeContext().getListState(
                new ListStateDescriptor<Integer>("vcListState", Types.INT)
            );
        }

        @Override
        public void processElement(
            WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out
        ) throws Exception {
            //vcListState.get();     //取出 list状态 本组的数据，是一个Iterable
            //vcListState.add();     // 向 list状态 本组 添加一个元素
            //vcListState.addAll();  // 向 list状态 本组 添加多个元素
            //vcListState.update();  // 更新 list状态 本组数据（覆盖）
            //vcListState.clear();   // 清空List状态 本组数据
            // 1.来一条，存到list状态里
            vcListState.add(value.getVc());
            // 2.从list状态拿出来(Iterable)，拷贝到一个List中排序，只留3个最大的
            Iterable<Integer> vcListIt = vcListState.get();
            // 2.1 拷贝到List中
            List<Integer> vcList = new ArrayList<>();
            for (Integer vc : vcListIt) {
                vcList.add(vc);
            }
            // 2.2 对List进行降序排序
            vcList.sort((o1, o2) -> o2 - o1);
            // 2.3 只保留最大的3个(list中的个数一定是连续变大，一超过3就立即清理即可)
            if (vcList.size() > 3) {
                // 将最后一个元素清除（第4个）
                vcList.remove(3);
            }
            out.collect("传感器id为" + value.getId() + ",最大的3个水位值=" + vcList);
            // 3.更新list状态
            vcListState.update(vcList);
        }
    }

    public static class MyMapStateProcFunc extends KeyedProcessFunction<String, WaterSensor, String> {
        MapState<Integer, Integer> vcCountMapState;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            vcCountMapState = getRuntimeContext().getMapState(
                new MapStateDescriptor<Integer, Integer>("vcCountMapState", Types.INT, Types.INT)
            );
        }

        @Override
        public void processElement(
            WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out
                    ) throws Exception {
            //vcCountMapState.get();          // 对本组的Map状态，根据key，获取value
            //vcCountMapState.contains();     // 对本组的Map状态，判断key是否存在
            //vcCountMapState.put( , );       // 对本组的Map状态，添加一个 键值对
            //vcCountMapState.putAll();       // 对本组的Map状态，添加多个 键值对
            //vcCountMapState.entries();      // 对本组的Map状态，获取所有键值对
            //vcCountMapState.keys();         // 对本组的Map状态，获取所有键
            //vcCountMapState.values();       // 对本组的Map状态，获取所有值
            //vcCountMapState.remove();       // 对本组的Map状态，根据指定key，移除键值对
            //vcCountMapState.isEmpty();      // 对本组的Map状态，判断是否为空
            //vcCountMapState.iterator();     // 对本组的Map状态，获取迭代器
            //vcCountMapState.clear();        // 对本组的Map状态，清空
            // 1.判断是否存在vc对应的key
            Integer vc = value.getVc();
            if (vcCountMapState.contains(vc)) {
                // 1.1 如果包含这个vc的key，直接对value+1
                Integer count = vcCountMapState.get(vc);
                vcCountMapState.put(vc, ++count);
            } else {
                // 1.2 如果不包含这个vc的key，初始化put进去
                vcCountMapState.put(vc, 1);
            }
            // 2.遍历Map状态，输出每个k-v的值
            StringBuilder outStr = new StringBuilder();
            outStr.append("======================================\n");
            outStr.append("传感器id为" + value.getId() + "\n");
            for (Map.Entry<Integer, Integer> vcCount : vcCountMapState.entries()) {
                outStr.append(vcCount.toString() + "\n");
            }
            outStr.append("======================================\n");
            out.collect(outStr.toString());
        }
    }

    public static class MyReducingStateProcFunc extends KeyedProcessFunction<String, WaterSensor, String> {
        ReducingState<Integer> vcSumReducingState;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            vcSumReducingState = getRuntimeContext()
                .getReducingState(
                    new ReducingStateDescriptor<Integer>(
                        "vcSumReducingState",
                        new ReduceFunction<Integer>() {
                            @Override
                            public Integer reduce(Integer value1, Integer value2) throws Exception {
                                return value1 + value2;
                            }
                        },
                        Types.INT
                    )
                );
        }

        @Override
        public void processElement(
            WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out
        ) throws Exception {
            //vcSumReducingState.get();   // 对本组的Reducing状态，获取结果
            //vcSumReducingState.add();   // 对本组的Reducing状态，添加数据
            //vcSumReducingState.clear(); // 对本组的Reducing状态，清空数据
            // 来一条数据，添加到 reducing状态里
            vcSumReducingState.add(value.getVc());
            Integer vcSum = vcSumReducingState.get();
            out.collect("传感器id为" + value.getId() + ",水位值总和=" + vcSum);
        }
    }

    public static class MyAggregatingStateProcFunc extends KeyedProcessFunction<String, WaterSensor, String> {
        AggregatingState<Integer, Double> vcAvgAggregatingState;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            vcAvgAggregatingState = getRuntimeContext()
                .getAggregatingState(
                    new AggregatingStateDescriptor<Integer, Tuple2<Integer, Integer>, Double>(
                        "vcAvgAggregatingState",
                        new AggregateFunction<Integer, Tuple2<Integer, Integer>, Double>() {
                            @Override
                            public Tuple2<Integer, Integer> createAccumulator() {
                                return Tuple2.of(0, 0);
                            }

                            @Override
                            public Tuple2<Integer, Integer> add(
                                Integer value, Tuple2<Integer, Integer> accumulator
                            ) {
                                return Tuple2.of(accumulator.f0 + value, accumulator.f1 + 1);
                            }

                            @Override
                            public Double getResult(Tuple2<Integer, Integer> accumulator) {
                                return accumulator.f0 * 1D / accumulator.f1;
                            }

                            @Override
                            public Tuple2<Integer, Integer> merge(
                                Tuple2<Integer, Integer> a, Tuple2<Integer, Integer> b
                            ) {
                                // return Tuple2.of(a.f0 + b.f0, a.f1 + b.f1);
                                return null;
                            }
                        },
                        Types.TUPLE(Types.INT, Types.INT))
                );
        }

        @Override
        public void processElement(
            WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out
        ) throws Exception {
            //vcAvgAggregatingState.get();    // 对 本组的聚合状态 获取结果
            //vcAvgAggregatingState.add();    // 对 本组的聚合状态 添加数据，会自动进行聚合
            //vcAvgAggregatingState.clear();  // 对 本组的聚合状态 清空数据
            // 将 水位值 添加到  聚合状态中
            vcAvgAggregatingState.add(value.getVc());
            // 从 聚合状态中 获取结果
            Double vcAvg = vcAvgAggregatingState.get();
            out.collect("传感器id为" + value.getId() + ",平均水位值=" + vcAvg);
        }
    }
}
