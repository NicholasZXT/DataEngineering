package FlinkDemos.c8_process_function;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 展示Flink底层处理函数API ProcessFunction 和 KeyedProcessFunction 使用。
 *
 * ProcessFunction 相关的包内容如下：
 * // org.apache.flink:flink-core
 * import org.apache.flink.api.common.functions.RichFunction;
 * import org.apache.flink.api.common.functions.AbstractRichFunction;
 * import org.apache.flink.api.common.functions.RichMapFunction;
 * import org.apache.flink.api.common.functions.RichFilterFunction;
 * import org.apache.flink.api.common.functions.RichFlatMapFunction;
 * import org.apache.flink.api.common.functions.RichAggregateFunction;
 * import org.apache.flink.api.common.functions.RichJoinFunction;
 * // Function接口
 * import org.apache.flink.api.common.functions.Function;
 * import org.apache.flink.api.common.functions.MapFunction;
 * import org.apache.flink.api.common.functions.FilterFunction;
 * import org.apache.flink.api.common.functions.FlatMapFunction;
 * import org.apache.flink.api.common.functions.ReduceFunction;
 * import org.apache.flink.api.common.functions.AggregateFunction;
 * import org.apache.flink.api.common.functions.CombineFunction;
 * import org.apache.flink.api.common.functions.JoinFunction;
 *
 * // org.apache.flink:flink-streaming-java
 * import org.apache.flink.streaming.api.functions.ProcessFunction;
 * import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
 * import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
 * import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
 * import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
 * import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
 * import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
 * import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
 * import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
 * import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
 * import org.apache.flink.streaming.api.functions.aggregation.AggregationFunction;
 */
public class ProcessFuncUsage {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 模拟数据源：用户点击流
        List<ClickEvent> events = Arrays.asList(
            new ClickEvent("user1", "view", 1000L),
            new ClickEvent("user2", "click", 2000L),
            new ClickEvent("user1", "buy", 3000L),
            new ClickEvent("user2", "view", 4000L),
            new ClickEvent("user1", "click", 5000L)
        );

        DataStream<ClickEvent> stream = env.fromCollection(events)
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<ClickEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                    .withTimestampAssigner((event, ts) -> event.timestamp)
            );

        // ====== 示例1: KeyedProcessFunction（按用户统计 + 定时器 + 侧输出） ======
        SingleOutputStreamOperator<String> keyedResult = stream
            .keyBy(event -> event.userId)
            .process(new UserBehaviorProcessFunction());
        // 打印主输出
        keyedResult.print("KEYED-Main");
        // 打印侧输出（buy事件）
        keyedResult.getSideOutput(BUY_EVENTS).print("KEYED-Side-Buy");

        // ====== 示例2: ProcessFunction（全局统计，仅用于对比） ======
        SingleOutputStreamOperator<String> globalResult = stream
            .process(new GlobalProcessFunction());
        globalResult.print("GLOBAL-Main");

        env.execute("ProcessFunction Usage");
    }

    // 模拟用户点击事件
    public static class ClickEvent {
        public String userId;
        public String action; // e.g., "view", "click", "buy"
        public long timestamp; // 事件时间（毫秒）

        public ClickEvent(String userId, String action, long timestamp) {
            this.userId = userId;
            this.action = action;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return String.format("ClickEvent{userId='%s', action='%s', ts=%d}", userId, action, timestamp);
        }
    }

    // 侧输出标签：用于输出"buy"行为
    static final OutputTag<ClickEvent> BUY_EVENTS = new OutputTag<ClickEvent>("buy-events") {};

    // ========================
    // KeyedProcessFunction：按用户处理
    // ========================
    public static class UserBehaviorProcessFunction extends KeyedProcessFunction<String, ClickEvent, String> {

        private ValueState<Integer> behaviorCount;
        private ValueState<Long> lastTimerTs;

        @Override
        public void open(Configuration parameters) {
            behaviorCount = getRuntimeContext().getState(
                new ValueStateDescriptor<>("behavior-count", Types.INT)
            );
            lastTimerTs = getRuntimeContext().getState(
                new ValueStateDescriptor<>("last-timer-ts", Types.LONG)
            );
        }

        @Override
        public void close() throws Exception {
            // 注意：close() 在 Task 结束时调用，但此时可能无法访问 keyed state 的所有 key
            // 实际生产中，通常不依赖 close() 做 per-key 清理（因为 key 太多）
            // 此处仅为演示目的，实际应通过定时器或 TTL 清理
            System.out.println("=== [KeyedProcessFunction] Closing... ===");
            // ⚠️ 注意：在 close() 中无法遍历所有 key 的状态！
            // 所以这里无法输出“每个用户最终行为次数”
            // 更合理的做法是在 onTimer 或使用 State TTL
        }

        @Override
        public void processElement(ClickEvent event, Context ctx, Collector<String> out) throws Exception {
            // 更新行为次数
            int count = behaviorCount.value() != null ? behaviorCount.value() : 0;
            behaviorCount.update(count + 1);

            // 注册一个事件时间定时器：在当前事件时间 + 2秒后触发
            long timerTs = event.timestamp + 2000L;
            Long oldTimer = lastTimerTs.value();
            if (oldTimer != null) {
                ctx.timerService().deleteEventTimeTimer(oldTimer);
            }
            ctx.timerService().registerEventTimeTimer(timerTs);
            lastTimerTs.update(timerTs);

            out.collect("User " + ctx.getCurrentKey() + " processed event: " + event);

            // 侧输出：如果是"buy"行为
            if ("buy".equals(event.action)) {
                ctx.output(BUY_EVENTS, event);
            }
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            String key = ctx.getCurrentKey();
            Integer count = behaviorCount.value();
            out.collect(String.format(
                "[TIMER] Triggered for user=%s at watermark=%d. Current count=%d. Timer was set by an event.",
                key, timestamp, count != null ? count : 0
            ));
        }

    }

    // ========================
    // ProcessFunction：全局处理（无 key）
    // ========================
    public static class GlobalProcessFunction extends ProcessFunction<ClickEvent, String> {
        // ❌ 不能用 ValueState（keyed state）
        //private ValueState<Integer> globalCount;
        // ✅ 改为普通变量（仅在 parallelism=1 时安全）
        private int globalCount = 0;

        @Override
        public void open(Configuration parameters) {
            // ValueState 是 KeyedState，不能用于全局处理函数
            //globalCount = getRuntimeContext().getState(
            //    new ValueStateDescriptor<>("global-count", Types.INT)
            //);
        }

        @Override
        public void close() throws Exception {
            //Integer finalCount = globalCount.value();
            //System.out.println("=== [GlobalProcessFunction] Final global count: " + (finalCount != null ? finalCount : 0) + " ===");
            System.out.println("=== [GlobalProcessFunction] Final global count: " + globalCount + " ===");
        }

        @Override
        public void processElement(ClickEvent event, Context ctx, Collector<String> out) throws Exception {
            //int count = globalCount.value() != null ? globalCount.value() : 0;
            //globalCount.update(count + 1);
            int count = globalCount;
            globalCount++; // 简单累加

            // 非 KeyedStream 无法注册 时间定时器 -------------- KEY
            //long procTimer = ctx.timerService().currentProcessingTime() + 1000L;
            //ctx.timerService().registerProcessingTimeTimer(procTimer);

            out.collect("Global processed event: " + event + ", total so far: " + (count + 1));
        }

        @Override
        public void onTimer(long timestamp, OnTimerContext ctx, Collector<String> out) throws Exception {
            out.collect("[GLOBAL TIMER] Fired at processing time: " + timestamp);
        }
    }
}
