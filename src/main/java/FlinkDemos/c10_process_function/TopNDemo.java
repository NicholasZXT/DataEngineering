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
 *
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
            .socketTextStream("localhost", 7890)
            .map(new TimeWindowAgg.WaterSensorMapFunction())
            .assignTimestampsAndWatermarks(
                WatermarkStrategy
                    .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                    .withTimestampAssigner((element, ts) -> element.getTs().longValue() * 1000L)
            );

        // ----------------------------------------------------------------------------
        // 方案一：不分区，使用一个窗口将所有数据汇集到一起，用hashmap存，key=vc，value=count值
        // 最近10秒= 窗口长度， 每5秒输出 = 滑动步长
        sensorDS.windowAll(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
            .process(new MyTopNPAWF())
            .print("TopN-with-AllWindow");

        // ----------------------------------------------------------------------------
        // 方案二：使用 KeyedProcessFunction实现
        /**
         * 1、按照 vc 做 keyBy，开窗，分别 count
         *    ==》 增量聚合，计算 count
         *    ==》 全窗口，对计算结果 count值封装,带上窗口结束时间的标签
         *          ==》 为了让同一个窗口时间范围的计算结果到一起去
         * 2、对同一个窗口范围的 count 值进行处理：排序、取前N个
         *    =》 按照 windowEnd 做 keyBy
         *    =》 使用 process，来一条调用一次，需要先分开存，用 HashMap{key=windowEnd,value=List}
         *      =》 使用定时器，对 存起来的结果 进行 排序、取前N个
         */
        // 1. 按照 vc 分组、开窗、聚合（增量计算+全量打标签）
        //  开窗聚合后，就是普通的流，没有了窗口信息，需要自己打上窗口的标记 windowEnd
        SingleOutputStreamOperator<Tuple3<Integer, Integer, Long>> windowAgg = sensorDS
            .keyBy(WaterSensor::getVc)
            .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
            .aggregate(
                new VcCountAgg(),
                new WindowResult()
            );
        // 2. 按照窗口标签（窗口结束时间）keyBy，保证同一个窗口时间范围的结果在一起，然后排序、取TopN
        windowAgg
            .keyBy(r -> r.f2)
            .process(new TopN(2))
            .print("TopN-with-KeyedProcessFunction");

        env.execute();
    }

    public static class MyTopNPAWF extends ProcessAllWindowFunction<WaterSensor, String, TimeWindow> {
        @Override
        public void process(
            Context context, Iterable<WaterSensor> elements, Collector<String> out
        ) throws Exception {
            // 定义一个hashmap用来存统计结果：key=vc，value=count值
            Map<Integer, Integer> vcCountMap = new HashMap<>();
            // 1.遍历数据, 统计各个vc出现的次数
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
            // 2.对 count值进行排序: 利用List来实现排序
            List<Tuple2<Integer, Integer>> datas = new ArrayList<>();
            for (Integer vc : vcCountMap.keySet()) {
                datas.add(Tuple2.of(vc, vcCountMap.get(vc)));
            }
            // 对List进行排序，根据count值 降序
            datas.sort(
                new Comparator<Tuple2<Integer, Integer>>() {
                    @Override
                    public int compare(Tuple2<Integer, Integer> o1, Tuple2<Integer, Integer> o2) {
                        // 降序， 后一个 减 前一个
                        return o2.f1 - o1.f1;
                    }
                }
            );
            // 3.取出 count最大的2个 vc
            StringBuilder outStr = new StringBuilder();
            outStr.append("================================\n");
            // 遍历排序后的 List，取出前2个， 考虑可能List不够2个的情况  ==》 List中元素的个数 和 2 取最小值
            for (int i = 0; i < Math.min(2, datas.size()); i++) {
                Tuple2<Integer, Integer> vcCount = datas.get(i);
                outStr.append("Top" + (i + 1) + "\n");
                outStr.append("vc=" + vcCount.f0 + "\n");
                outStr.append("count=" + vcCount.f1 + "\n");
                outStr.append("窗口结束时间=" + DateFormatUtils.format(context.window().getEnd(), "yyyy-MM-dd HH:mm:ss.SSS") + "\n");
                outStr.append("================================\n");
            }
            out.collect(outStr.toString());
        }
    }


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
     * 第一个：输入类型 = 增量函数的输出  count值，Integer
     * 第二个：输出类型 = Tuple3(vc，count，windowEnd) ,带上 窗口结束时间 的标签
     * 第三个：key类型 ， vc，Integer
     * 第四个：窗口类型
     */
    public static class WindowResult extends ProcessWindowFunction<Integer, Tuple3<Integer, Integer, Long>, Integer, TimeWindow> {
        @Override
        public void process(
            Integer key, Context context, Iterable<Integer> elements, Collector<Tuple3<Integer, Integer, Long>> out
        ) throws Exception {
            // 迭代器里面只有一条数据，next一次即可
            Integer count = elements.iterator().next();
            long windowEnd = context.window().getEnd();
            out.collect(Tuple3.of(key, count, windowEnd));
        }
    }

    public static class TopN extends KeyedProcessFunction<Long, Tuple3<Integer, Integer, Long>, String> {
        // 存不同窗口的 统计结果，key=windowEnd，value=list数据
        private Map<Long, List<Tuple3<Integer, Integer, Long>>> dataListMap;
        // 要取的Top数量
        private int threshold;

        public TopN(int threshold) {
            this.threshold = threshold;
            dataListMap = new HashMap<>();
        }

        @Override
        public void processElement(
            Tuple3<Integer, Integer, Long> value, Context ctx, Collector<String> out
        ) throws Exception {
            // 进入这个方法，只是一条数据，要排序，得到齐才行 ===》 存起来，不同窗口分开存
            // 1. 存到HashMap中
            Long windowEnd = value.f2;
            if (dataListMap.containsKey(windowEnd)) {
                // 1.1 包含vc，不是该vc的第一条，直接添加到List中
                List<Tuple3<Integer, Integer, Long>> dataList = dataListMap.get(windowEnd);
                dataList.add(value);
            } else {
                // 1.1 不包含vc，是该vc的第一条，需要初始化list
                List<Tuple3<Integer, Integer, Long>> dataList = new ArrayList<>();
                dataList.add(value);
                dataListMap.put(windowEnd, dataList);
            }
            // 2. 注册一个定时器， windowEnd+1ms即可（
            // 同一个窗口范围，应该同时输出，只不过是一条一条调用processElement方法，只需要延迟1ms即可
            ctx.timerService().registerEventTimeTimer(windowEnd + 1);

        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            super.onTimer(timestamp, ctx, out);
            // 定时器触发，同一个窗口范围的计算结果攒齐了，开始 排序、取TopN
            Long windowEnd = ctx.getCurrentKey();
            // 1. 排序
            List<Tuple3<Integer, Integer, Long>> dataList = dataListMap.get(windowEnd);
            dataList.sort(new Comparator<Tuple3<Integer, Integer, Long>>() {
                @Override
                public int compare(Tuple3<Integer, Integer, Long> o1, Tuple3<Integer, Integer, Long> o2) {
                    // 降序， 后 减 前
                    return o2.f1 - o1.f1;
                }
            });
            // 2. 取TopN
            StringBuilder outStr = new StringBuilder();
            outStr.append("================================\n");
            // 遍历 排序后的 List，取出前 threshold 个， 考虑可能List不够2个的情况  ==》 List中元素的个数 和 2 取最小值
            for (int i = 0; i < Math.min(threshold, dataList.size()); i++) {
                Tuple3<Integer, Integer, Long> vcCount = dataList.get(i);
                outStr.append("Top" + (i + 1) + "\n");
                outStr.append("vc=" + vcCount.f0 + "\n");
                outStr.append("count=" + vcCount.f1 + "\n");
                outStr.append("窗口结束时间=" + vcCount.f2 + "\n");
                outStr.append("================================\n");
            }
            // 用完的List，及时清理，节省资源
            dataList.clear();
            out.collect(outStr.toString());
        }
    }
}
