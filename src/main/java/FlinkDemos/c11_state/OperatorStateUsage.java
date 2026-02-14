package FlinkDemos.c11_state;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.util.Collector;

/**
 * 演示 算子状态 使用
 */
public class OperatorStateUsage {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //env.setParallelism(2);

        // ---------------- ListState 使用 ----------------
        //showListStateUsage(env);

        // ---------------- 联合ListState 使用 ----------------
        //showUnionListStateUsage(env);

        // ---------------- BroadcastState 使用 ----------------
        showBroadcastStateUsage(env);

        env.execute();
    }


    public static void showListStateUsage(StreamExecutionEnvironment env) throws Exception {
        // 从集合中创建源数据
        DataStreamSource<UserEvent> stream = env.fromElements(
            new UserEvent("U1", "S1"),
            new UserEvent("U2", "S2"),
            new UserEvent("U1", "S3"),
            new UserEvent("U2", "S4"),
            new UserEvent("U1", "S5"),
            new UserEvent("U2", "S6"),
            new UserEvent("U1", "S7"),
            new UserEvent("U2", "S8")
        );

        // 虽然算子状态的演示不需要进行分区，但是由于 RichMapFunction 是

        // 在map算子中计算数据的个数
        stream
            //.keyBy(Event::getKey)
            .map(new TaskCounterMapFunction()).name("TaskCounter")
            .print("[ListStateUsage]");
    }

    public static void showUnionListStateUsage(StreamExecutionEnvironment env) throws Exception {
        // 从集合中创建源数据
        DataStreamSource<UserEvent> stream = env.fromElements(
            new UserEvent("K1", "S1"),
            new UserEvent("K2", "S2"),
            new UserEvent("K1", "S3"),
            new UserEvent("K2", "S4"),
            new UserEvent("K1", "S5"),
            new UserEvent("K2", "S6"),
            new UserEvent("K1", "S7"),
            new UserEvent("K2", "S8")
        );
        // TODO 联合ListState 使用
    }

    /**
     * 演示 BroadcastState 使用
     */
    public static void showBroadcastStateUsage(StreamExecutionEnvironment env) throws Exception {
        // ========== 1. 主数据流：从集合生成 ==========
        List<UserEvent> events = Arrays.asList(
            new UserEvent("user1", "login"),
            new UserEvent("user2", "click"),
            new UserEvent("user3", "buy"),
            new UserEvent("user1", "logout")
        );
        DataStreamSource<UserEvent> userStream = env.fromCollection(events);

        // ========== 2. 规则流：从集合生成 ==========
        List<FilterRule> rules = Arrays.asList(
            new FilterRule("rule1", "login", false),     // 添加规则：匹配 login
            new FilterRule("rule2", "buy",  false),      // 添加规则：匹配 buy
            new FilterRule("rule1", "login", true)       // 删除 rule1
        );
        DataStreamSource<FilterRule> ruleStream = env.fromCollection(rules);

        // ========== 3. 定义 BroadcastState 的描述符 ==========
        MapStateDescriptor<String, FilterRule> ruleStateDescriptor = new MapStateDescriptor<>("filter-rules", String.class, FilterRule.class);

        // ========== 4. 将规则流广播 ==========
        BroadcastStream<FilterRule> broadcastRules = ruleStream.broadcast(ruleStateDescriptor);

        // ========== 5. 连接主流和广播流 ==========
        BroadcastConnectedStream<UserEvent, FilterRule> connectedStream = userStream.connect(broadcastRules);
        // 调用处理自定义 BroadcastProcessFunction 函数处理广播流
        connectedStream
            .process(new RuleMatchingBroadcastFunction(ruleStateDescriptor))
            .print("[BroadcastStateUsage]");
    }


    /**
     * 模拟用户事件类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserEvent {
        private String userId;
        private String action;
    }

    // 广播规则类：过滤条件
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterRule {
        public String ruleId;
        public String requiredAction; // 要匹配的动作
        public boolean isDelete;      // 是否删除规则
    }

    /**
     *  使用算子状态实现局部计数器。
     *  必须要实现 CheckpointedFunction 接口；
     *  这里推荐使用 RichMapFunction抽象类 而不是 MapFunction接口
     */
    //public static class TaskCounterMapFunction implements MapFunction<UserEvent, Long>, CheckpointedFunction {
    public static class TaskCounterMapFunction extends RichMapFunction<UserEvent, Long> implements CheckpointedFunction {

        // 局部计数变量
        private Long localCount = 0L;
        // 算子状态引用变量
        // 这里的 ListState 是当做单值状态来使用的，其中始终只有一个元素 —— 当前task处理元素个数的最新累计值
        // 因为算子状态没有 ValueState
        private ListState<Long> counterState;

        /**
         * 实现 map 方法。
         * 此方法中对处理的事件进行局部计数
         */
        @Override
        public Long map(UserEvent value) throws Exception {
            localCount++;
            System.out.println("map is called for event"+ value +" with localCount: " + localCount);
            return localCount;
        }

        // --------------- CheckpointedFunction 实现 -------------
        /**
         * 初始化方法：程序启动和恢复时，从算子状态中把数据添加到本地变量，每个子任务调用一次
         */
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            System.out.println(getRuntimeContext().getTaskName() + ".initializeState() is called...");
            // 1. 从 上下文 初始化 算子状态
            ListStateDescriptor<Long> counterStateDesc = new ListStateDescriptor<>("local-counter", Types.LONG);
            counterState = context
                .getOperatorStateStore()           // 从上下文对象中获取 OperatorStateStore
                .getListState(counterStateDesc);   // 借助 OperatorStateStore 来获取算子状态

            // 2. 从 算子状态中 把数据 拷贝到 本地变量
            this.localCount = 0L;
            if (context.isRestored()) {
                // 实际上 counterState 里始终只有一个值
                for (Long c : counterState.get()) {
                    localCount += c;
                }
            }
        }

        /**
         * 状态快照保存：将 本地变量 拷贝到 算子状态中，每次执行checkpoint时调用
         */
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            System.out.println(getRuntimeContext().getTaskName() + ".snapshotState() is called...");
            // 下面的两个操作保证 ListState 里始终只有一个元素——当前task处理元素格式的最新累计值
            // 1. 清空算子状态
            counterState.clear();
            // 2. 将 本地变量 添加到 算子状态 中
            counterState.add(localCount);
        }
    }

    /**
     * 自定义 BroadcastProcessFunction
     */
    public static class RuleMatchingBroadcastFunction extends BroadcastProcessFunction<UserEvent, FilterRule, String> {

        // 这个成员变量用于在初始化时接受被广播流的描述符
        private final MapStateDescriptor<String, FilterRule> ruleStateDescriptor;

        public RuleMatchingBroadcastFunction(MapStateDescriptor<String, FilterRule> ruleStateDescriptor) {
            this.ruleStateDescriptor = ruleStateDescriptor;
        }

        /**
         * 处理主数据流中的每个事件。
         *   - event 是当前的主流事件
         *   - ctx 是上下文对象，可以通过它获取广播状态
         *   - out 是输出结果收集器
         * 注意：主流（数据流） 只能 读取 广播状态，不能修改。
         */
        @Override
        public void processElement(UserEvent event, ReadOnlyContext ctx, Collector<String> out) throws Exception {
            // 通过 广播流描述符 + 上下文对象 来获取 只读的 广播流
            // 广播流描述符 一般需要通过 当前类的构造函数传入，并使用一个成员变量保存
            // 再次提醒，这里只能读广播流，不能修改
            Iterable<Map.Entry<String, FilterRule>> rules = ctx.getBroadcastState(ruleStateDescriptor).immutableEntries();

            // 遍历广播流，并处理当前的主流元素
            // 这里可能需要注意广播流此时还没有元素
            boolean matched = false;
            for (Map.Entry<String, FilterRule> entry : rules) {
                if (event.action.equals(entry.getValue().requiredAction)) {
                    matched = true;
                    break;
                }
            }
            if (matched) {
                out.collect("[MATCH] " + event + " matched current rules");
            } else {
                out.collect("[NO MATCH] " + event);
            }
        }

        /**
         * 处理广播流中的规则更新
         *   - value 是当前的广播流元素
         *   - ctx 是上下文对象，可以通过它获取广播状态
         *   - out 是输出结果收集器
         * 只有广播流才能修改 广播状态
         */
        @Override
        public void processBroadcastElement(FilterRule rule, Context ctx, Collector<String> out) throws Exception {
            BroadcastState<String, FilterRule> state = ctx.getBroadcastState(ruleStateDescriptor);
            if (rule.isDelete) {
                state.remove(rule.ruleId);
                System.out.println(">>> Rule removed: " + rule.ruleId);
            } else {
                state.put(rule.ruleId, rule);
                System.out.println(">>> Rule added/updated: " + rule.ruleId + " -> " + rule.requiredAction);
            }
        }
    }

}
