package FlinkDemos.c10_process_function;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;
import org.apache.commons.lang3.time.DateFormatUtils;
import java.time.Duration;
import java.util.*;

import FlinkDemos.beans.WaterSensor;
import FlinkDemos.c8_window.TimeWindowAgg;

/**
 * 实时统计一段时间内的出现次数最多的水位。
 * 例如统计最近 10 秒钟内出现次数最多的两个水位，并且每 5 秒钟更新一次
 */
public class TopNDemo {
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
            .socketTextStream("localhost", 9700)
            .map(new TimeWindowAgg.WaterSensorMapFunction())
            // 分配水位线
            .assignTimestampsAndWatermarks(
                WatermarkStrategy
                    .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                    .withTimestampAssigner((element, ts) -> element.getTs().longValue() * 1000L)
            );
        //sensorDS.print("sensorDS");

        // ----------------------------------------------------------------------------
        // 方案一：不分区，使用一个窗口将所有数据汇集到一起，用hashmap存状态 {key=vc, value=count}
        // 窗口长度 = 最近4秒, 滑动步长 = 每2秒输出
        //sensorDS.windowAll(SlidingEventTimeWindows.of(Time.seconds(4), Time.seconds(2)))
        //    .process(new MyTopNPAWF())
        //    .print("TopN-with-AllWindow");

        // ----------------------------------------------------------------------------
        // 方案二：使用 KeyedProcessFunction实现
        /**
         * 1、按照 vc 做 keyBy，开窗，分别 count
         *  1.1 增量聚合，计算 count
         *  1.2 全窗口，对计算结果 count值封装,带上窗口结束时间的标签，为了让同一个窗口时间范围的计算结果到一起去
         * 2、对同一个窗口范围的 count 值进行处理：排序、取前N个
         *  2.1 按照 windowEnd 做 keyBy
         *  2.2 使用 process，来一条调用一次，需要先分开存，用 HashMap{key=windowEnd, value=List}
         *  2.3 使用定时器，对 存起来的结果 进行 排序、取前N个
         */
        // 1. 按照 vc 分组、开窗、聚合（增量计算+全量打标签）
        //  开窗聚合后，就是普通的流，没有了窗口信息，需要自己打上窗口的标记 windowEnd
        SingleOutputStreamOperator<Tuple3<Integer, Integer, Long>> sensorDSW = sensorDS
            .keyBy(WaterSensor::getVc)
            .window(SlidingEventTimeWindows.of(Time.seconds(4), Time.seconds(2)))
            .aggregate(
                new VcCountAgg(),
                new WindowResult()
            );
        // 2. 按照窗口标签（窗口结束时间）keyBy，保证同一个窗口时间范围的结果在一起，然后排序、取TopN
        sensorDSW
            .keyBy(r -> r.f2)
            .process(new TopN(2))
            .print("TopN-with-KeyedProcessFunction");

        env.execute();
    }

    public static class MyTopNPAWF extends ProcessAllWindowFunction<WaterSensor, String, TimeWindow> {
        @Override
        public void process(Context context, Iterable<WaterSensor> elements, Collector<String> out) throws Exception {
            // 1. 遍历数据, 统计各个vc出现的次数，存放在一个 HashMap 里
            // 定义一个hashmap用来存统计结果: key=vc, value=count
            Map<Integer, Integer> vcCountMap = new HashMap<>();
            for (WaterSensor element : elements) {
                Integer vc = element.getVc();
                if (vcCountMap.containsKey(vc)) {
                    // 1.1 key存在，不是这个key的第一条数据，直接累加
                    vcCountMap.put(vc, vcCountMap.get(vc) + 1);
                } else {
                    // 1.2 key不存在，初始化
                    vcCountMap.put(vc, 1);
                }
            }

            // 2. 对 count值进行排序: 利用List来实现排序
            List<Tuple2<Integer, Integer>> counts = new ArrayList<>();
            for (Integer vc : vcCountMap.keySet()) {
                counts.add(Tuple2.of(vc, vcCountMap.get(vc)));
            }
            // 对List进行排序，根据count值 降序
            counts.sort(
                new Comparator<Tuple2<Integer, Integer>>() {
                    @Override
                    public int compare(Tuple2<Integer, Integer> o1, Tuple2<Integer, Integer> o2) {
                        // 降序， 后一个 减 前一个
                        return o2.f1 - o1.f1;
                    }
                }
            );

            // 3. 取出 count最大的2个 vc
            StringBuilder outStr = new StringBuilder();
            outStr.append("\n--------------------------------\n");
            // 遍历排序后的 List，取出前2个， 考虑可能List不够2个的情况  ==》 List中元素的个数 和 2 取最小值
            for (int i = 0; i < Math.min(2, counts.size()); i++) {
                Tuple2<Integer, Integer> vcCount = counts.get(i);
                outStr.append("Top" + (i + 1) + "\n");
                outStr.append("vc=" + vcCount.f0 + "\n");
                outStr.append("count=" + vcCount.f1 + "\n");
                outStr.append("窗口结束时间=" + DateFormatUtils.format(context.window().getEnd(), "yyyy-MM-dd HH:mm:ss.SSS") + "\n");
                outStr.append("--------------------------------\n");
            }
            out.collect(outStr.toString());
        }
    }


    /**
     * 1、增量聚合：计算 每个key(vc值)下的 count
     */
    public static class VcCountAgg implements AggregateFunction<WaterSensor, Integer, Integer> {
        @Override
        public Integer createAccumulator() {
            return 0;
        }

        @Override
        public Integer add(WaterSensor value, Integer accumulator) {
            return accumulator + 1;
        }

        @Override
        public Integer getResult(Integer accumulator) {
            return accumulator;
        }

        @Override
        public Integer merge(Integer a, Integer b) {
            return null;
        }
    }

    /**
     * 泛型如下：
     * 第一个：输入类型 = 增量聚合函数的输出 count值: Integer
     * 第二个：输出类型 = Tuple3(vc, count, windowEnd), 带上 窗口结束时间 的标签
     * 第三个：key类型 = vc: Integer
     * 第四个：窗口类型
     */
    public static class WindowResult extends ProcessWindowFunction<Integer, Tuple3<Integer, Integer, Long>, Integer, TimeWindow> {
        @Override
        public void process(
            Integer key, Context context, Iterable<Integer> elements, Collector<Tuple3<Integer, Integer, Long>> out
        ) throws Exception {
            // 每个key内部调用 VcCountAgg 增量聚合之后，最后只有一个总的count值，因此迭代器里面只有一条数据，next一次即可
            Integer vcCount = elements.iterator().next();
            long windowEnd = context.window().getEnd();
            // 输出的时候，带上当前的 key 和 windowEnd，用于下一阶段的处理
            out.collect(Tuple3.of(key, vcCount, windowEnd));
        }
    }

    /**
     * 泛型参数 KeyedProcessFunction<KEY, IN, OUT> 如下：
     * <KEY> – Type of the key.
     * <IN> – Type of the input elements.
     * <OUT> – Type of the output elements.
     */
    public static class TopN extends KeyedProcessFunction<Long, Tuple3<Integer, Integer, Long>, String> {
        // 存不同窗口的 统计结果 {key=windowEnd, value=数据List}
        private Map<Long, List<Tuple3<Integer, Integer, Long>>> windowCountMap;
        // 要取的Top数量
        private int threshold;

        public TopN(int threshold) {
            this.threshold = threshold;
            windowCountMap = new HashMap<>();
        }

        /**
         * 处理 按键分区 后流中的每个元素
         * @param value 当前待处理的元素，对应于 IN 类型
         * @param ctx 上下文对象，提供访问当前键、时间戳、定时器服务等功能，类型是 Context
         * @param out 用于收集输出结果的收集器，类型是 Collector<OUT>
         * @throws Exception
         */
        @Override
        public void processElement(
            Tuple3<Integer, Integer, Long> value, Context ctx, Collector<String> out
        ) throws Exception {
            // 每个 Key 的窗口中每条元素都会调用此方法
            // 1. 这里的 Key 是 windowEnd 时间，每个Key（WindowEnd）里是不同水位的 count，将不同水位的 count 存放在 HashMap中
            Long windowEnd = value.f2;
            if (windowCountMap.containsKey(windowEnd)) {
                // 1.1 包含vc，不是该vc的第一条，直接添加到List中
                List<Tuple3<Integer, Integer, Long>> vcCountList = windowCountMap.get(windowEnd);
                vcCountList.add(value);
            } else {
                // 1.1 不包含vc，是该vc的第一条，需要初始化list
                List<Tuple3<Integer, Integer, Long>> vcCountList = new ArrayList<>();
                vcCountList.add(value);
                windowCountMap.put(windowEnd, vcCountList);
            }
            // 2. 注册一个定时器， windowEnd + 1ms即可
            // 同一个窗口范围，应该同时输出，只不过是一条一条调用processElement方法，只需要延迟1ms即可
            ctx.timerService().registerEventTimeTimer(windowEnd + 1);
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            super.onTimer(timestamp, ctx, out);
            // 定时器触发，同一个窗口范围的计算结果攒齐了，开始 排序、取TopN
            Long windowEnd = ctx.getCurrentKey();
            // 1. 排序
            List<Tuple3<Integer, Integer, Long>> vcCountList = windowCountMap.get(windowEnd);
            vcCountList.sort(new Comparator<Tuple3<Integer, Integer, Long>>() {
                @Override
                public int compare(Tuple3<Integer, Integer, Long> o1, Tuple3<Integer, Integer, Long> o2) {
                    // 降序， 后 减 前
                    return o2.f1 - o1.f1;
                }
            });
            // 2. 取TopN
            StringBuilder outStr = new StringBuilder();
            outStr.append("\n================================\n");
            // 遍历 排序后的 List，取出前 threshold 个，考虑可能List不够2个的情况，因此取 List中元素的个数 和 2 的较小值
            for (int i = 0; i < Math.min(threshold, vcCountList.size()); i++) {
                Tuple3<Integer, Integer, Long> vcCount = vcCountList.get(i);
                outStr.append("vc=" + vcCount.f0 + "\n");
                outStr.append("count=" + vcCount.f1 + "\n");
                outStr.append("Top" + (i + 1) + "\n");
                outStr.append("窗口结束时间=" + vcCount.f2 + "\n");
                outStr.append("================================\n");
            }
            // 用完的List，及时清理，节省资源
            vcCountList.clear();
            out.collect(outStr.toString());
        }
    }
}
