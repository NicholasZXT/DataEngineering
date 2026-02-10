package FlinkDemos.c10_process_function;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 展示Flink底层处理函数API ProcessWindowFunction 和 ProcessAllWindowFunction 使用
 */
public class ProcessFuncWindowUsage {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 模拟用户点击流（时间戳单位：毫秒）
        List<ClickEvent> events = Arrays.asList(
            new ClickEvent("user1", "view", 10_000L),  // 窗口 [0, 120_000)
            new ClickEvent("user2", "click", 30_000L),
            new ClickEvent("user1", "buy", 60_000L),
            new ClickEvent("user2", "view", 90_000L),
            new ClickEvent("user3", "click", 150_000L),  // 窗口 [120_000, 240_000)
            new ClickEvent("user1", "click", 200_000L)
        );

        DataStream<ClickEvent> stream = env.fromCollection(events)
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<ClickEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                    .withTimestampAssigner((event, ts) -> event.timestamp)
            );

        // ====== 1. Keyed Window: ProcessWindowFunction ======
        // 统计每个用户在窗口大小为2分钟的滚动窗口内的行为次数
        SingleOutputStreamOperator<String> keyedResult = stream
            .keyBy(event -> event.userId)
            .window(TumblingEventTimeWindows.of(Time.minutes(2)))
            .process(new UserCountProcessWindowFunction());
        keyedResult.print("KEYED-WINDOW");

        // 手动在 ProcessWindowFunction 内部输出侧流（兼容所有 Flink 版本）
        keyedResult.getSideOutput(BUY_EVENTS).print("SIDE-OUTPUT-BUY");

        // ====== 2. Global Window: ProcessAllWindowFunction ======
        // 统计2分钟全局窗口内所有用户的行为次数
        SingleOutputStreamOperator<String> globalResult = stream
            .windowAll(TumblingEventTimeWindows.of(Time.minutes(2)))
            .process(new GlobalCountProcessAllWindowFunction());
        globalResult.print("GLOBAL-WINDOW");

        env.execute("Window ProcessFunction Demo");
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

    // 侧输出标签
    static final OutputTag<ClickEvent> BUY_EVENTS = new OutputTag<ClickEvent>("buy-events") {};

    // =============================================
    // ProcessWindowFunction: per-user window count
    // =============================================
    public static class UserCountProcessWindowFunction
        extends ProcessWindowFunction<ClickEvent, String, String, TimeWindow> {

        @Override
        public void process(String userId, Context context, Iterable<ClickEvent> elements, Collector<String> out) throws Exception {
            TimeWindow window = context.window();
            System.out.println("\n[KEYED] Processing window [" + window.getStart() + ", " + window.getEnd() + ") for key=" + userId);
            System.out.println("[KEYED] Current watermark: " + context.currentWatermark());
            System.out.println("[KEYED] Current processing time: " + context.currentProcessingTime());

            // 使用 window state 统计行为次数（演示状态用法）
            ValueState<Integer> countState = context.windowState()
                .getState(new ValueStateDescriptor<>("count", Types.INT));
            int count = 0;
            for (ClickEvent event : elements) {
                count++;
                // 侧输出 "buy" 事件
                if ("buy".equals(event.action)) {
                    context.output(BUY_EVENTS, event);
                }
            }
            countState.update(count);

            out.collect(String.format(
                "[KEYED] User %s had %d actions in window [%d, %d)",
                userId, count, window.getStart(), window.getEnd()
            ));
        }

        @Override
        public void clear(Context context) throws Exception {
            TimeWindow window = context.window();
            System.out.println("[KEYED-CLEAR] Clearing state for window [" + window.getStart() + ", " + window.getEnd() + ")");

            ValueState<Integer> countState = context.windowState()
                .getState(new ValueStateDescriptor<>("count", Types.INT));
            countState.clear();
        }
    }

    // =============================================
    // ProcessAllWindowFunction: global window count
    // =============================================
    public static class GlobalCountProcessAllWindowFunction
        extends ProcessAllWindowFunction<ClickEvent, String, TimeWindow> {

        @Override
        public void process(Context context, Iterable<ClickEvent> elements, Collector<String> out) throws Exception {
            TimeWindow window = context.window();
            System.out.println("\n[GLOBAL] Processing global window [" + window.getStart() + ", " + window.getEnd() + ")");
            // 全局窗口的 Context 对象没有下面两个方法
            //System.out.println("[GLOBAL] Current watermark: " + context.currentWatermark());
            //System.out.println("[GLOBAL] Current processing time: " + context.currentProcessingTime());

            ValueState<Integer> globalCount = context.windowState()
                .getState(new ValueStateDescriptor<>("global-count", Types.INT));
            int count = 0;
            for (ClickEvent event : elements) {
                count++;
            }
            globalCount.update(count);

            out.collect(String.format(
                "[GLOBAL] Total %d actions in window [%d, %d)",
                count, window.getStart(), window.getEnd()
            ));
        }

        @Override
        public void clear(Context context) throws Exception {
            TimeWindow window = context.window();
            System.out.println("[GLOBAL-CLEAR] Clearing state for global window [" + window.getStart() + ", " + window.getEnd() + ")");

            ValueState<Integer> globalCount = context.windowState()
                .getState(new ValueStateDescriptor<>("global-count", Types.INT));
            globalCount.clear();
        }
    }
}
