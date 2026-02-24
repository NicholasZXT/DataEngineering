package FlinkDemos.cep;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Flink CEP 最小使用示例
 * 场景：检测同一用户在5秒内连续2次登录失败
 * ┌─────────────────────────────────────────────────────────────────┐
 * │                    Flink CEP 处理流程                            │
 * ├─────────────────────────────────────────────────────────────────┤
 * │                                                                 │
 * │  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐  │
 * │  │ 1.定义    │ →  │ 2.定义    │ →  │ 3.应用   │ →  │ 4.处理   │  │
 * │  │ 事件类    │    │ Pattern  │    │ 到流上    │    │ 匹配结果 │  │
 * │  └──────────┘    └──────────┘    └──────────┘    └──────────┘  │
 * │       ↓               ↓               ↓               ↓         │
 * │   equals()       begin()         CEP.pattern()    select()     │
 * │   hashCode()     where()         keyBy()          process()    │
 * │                    times()                                        │
 * │                    within()                                       │
 * │                                                                 │
 * └─────────────────────────────────────────────────────────────────┘
 */
public class FlinkCepExample {
    // ==================== 1. 定义事件类 ====================
    /**
     * 登录事件类
     * ⚠️ 重要：必须实现 equals() 和 hashCode() 方法
     * 原因：Flink CEP内部使用这些方法进行事件比较和状态匹配
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginEvent {
        public String userId;      // 用户ID
        public String eventType;   // 事件类型（"success"或"fail"）
        public Long timestamp;     // 事件时间戳

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LoginEvent that = (LoginEvent) o;
            //return userId != null ? userId.equals(that.userId) : that.userId == null;
            return Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return userId != null ? userId.hashCode() : 0;
        }
    }

    // ==================== 2. 定义告警输出类 ====================
    /**
     * 登录失败告警类（匹配成功后的输出结果）
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginFailWarning {
        public String userId;
        public Integer failCount;
        public Long startTime;
        public Long endTime;
        public String warningType;  // 告警类型

        @Override
        public String toString() {
            return "⚠️ [" + warningType + "] 登录失败告警: 用户=" + userId +
                    ", 失败次数=" + failCount +
                    ", 时间范围=[" + startTime + ", " + endTime + "]";
        }
    }

    public static void main(String[] args) throws Exception {
        // ==================== 3. 创建执行环境 ====================
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);  // 设置并行度为1，方便观察输出

        // ==================== 4. 创建输入数据流 ====================
        // 模拟登录事件数据流
        DataStream<LoginEvent> loginStream = env.fromElements(
                new LoginEvent("user_001", "fail", 1000L),   // 第1次失败
                new LoginEvent("user_001", "fail", 2000L),   // 第2次失败 → 触发告警
                new LoginEvent("user_002", "fail", 3000L),   // 其他用户失败
                new LoginEvent("user_001", "success", 4000L), // 成功登录
                new LoginEvent("user_003", "fail", 5000L),   // 其他用户失败
                new LoginEvent("user_003", "fail", 5500L)    // 第2次失败 → 触发告警
        );

        // 设置事件时间语义和水位线（生产环境必备）
        DataStream<LoginEvent> eventTimeStream = loginStream
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<LoginEvent>forMonotonousTimestamps()  // 单调递增时间戳
                                .withTimestampAssigner((event, timestamp) -> event.timestamp)
                );

        // ==================== 5. 定义CEP模式（核心步骤） ====================
        /**
         * Pattern 定义说明：
         * - Pattern.begin(): 定义模式的起始事件
         * - .where(): 添加过滤条件
         * - .times(2): 要求连续匹配2次
         * - .within(Time.seconds(5)): 在5秒时间窗口内完成匹配
         */
        Pattern<LoginEvent, LoginEvent> failPattern = Pattern
                .<LoginEvent>begin("firstFail")  // 定义第一个模式，命名为"firstFail"
                .where(new SimpleCondition<LoginEvent>() {  // 添加过滤条件
                    @Override
                    public boolean filter(LoginEvent event) {
                        return "fail".equals(event.eventType);  // 只匹配登录失败事件
                    }
                })
                .times(2)  // 连续匹配2次
                .within(Time.seconds(5));  // 在5秒时间窗口内

        // ==================== 6. 将模式应用到数据流上 ====================
        /**
         * CEP.pattern() 方法说明：
         * - 输入：DataStream + Pattern
         * - 输出：PatternStream（包含所有部分匹配和完全匹配的状态）
         * - 内部原理：为每个key维护一个NFA（非确定性有限自动机）状态机
         */
        PatternStream<LoginEvent> patternStream = CEP.pattern(
                eventTimeStream.keyBy(LoginEvent::getUserId),  // 按用户ID分组，确保同一用户的事件被同一实例处理
                failPattern
        );

        // ==================== 7. 处理匹配结果 ====================
        // 7.1 使用 select() 方法处理匹配结果
        /**
         * select() 方法说明：
         * - 当模式完全匹配时触发
         * - 输入：Map<String, List<LoginEvent>>，key是模式名称，value是匹配到的事件列表
         * - 输出：自定义的告警对象
         */
        DataStream<LoginFailWarning> warningStreamWithSelect = patternStream.select(
                new PatternSelectFunction<LoginEvent, LoginFailWarning>() {
                    @Override
                    public LoginFailWarning select(Map<String, List<LoginEvent>> pattern) {
                        // 从匹配结果中提取事件：key是模式名称，value是匹配到的事件列表
                        List<LoginEvent> failEvents = pattern.get("firstFail");

                        // 提取第一个和最后一个事件的时间
                        Long startTime = failEvents.get(0).timestamp;
                        Long endTime = failEvents.get(failEvents.size() - 1).timestamp;

                        // 创建告警对象
                        return new LoginFailWarning(
                                failEvents.get(0).userId,
                                failEvents.size(),
                                startTime,
                                endTime,
                                "匹配成功"
                        );
                    }
                }
        );

        // 7.2 使用 process()方法 + PatternProcessFunction 处理匹配结果 —— 推荐使用这个API
        /**
         * PatternProcessFunction 核心方法说明：
         *  - processMatch()
         *    - 当模式完全匹配时调用（相当于select的功能）
         *    - 参数：匹配到的事件Map + 上下文 + 收集器
         */
        SingleOutputStreamOperator<LoginFailWarning> warningStreamWithProcess = patternStream.process(
                new PatternProcessFunction<LoginEvent, LoginFailWarning>() {
                    /**
                     * 处理完全匹配的情况
                     * @param map 匹配到的事件，key是模式名称，value是事件列表
                     * @param ctx 上下文对象，可访问时间、侧输出等
                     * @param out 结果收集器
                     */
                    @Override
                    public void processMatch(
                            Map<String, List<LoginEvent>> map,
                            Context ctx,
                            Collector<LoginFailWarning> out
                    ) throws Exception {
                        List<LoginEvent> failEvents = map.get("firstFail");

                        Long startTime = failEvents.get(0).timestamp;
                        Long endTime = failEvents.get(failEvents.size() - 1).timestamp;

                        // 输出匹配成功的告警
                        out.collect(new LoginFailWarning(
                                failEvents.get(0).userId,
                                failEvents.size(),
                                startTime,
                                endTime,
                                "匹配成功"
                        ));
                    }
                }
        );

        // ==================== 8. 输出结果 ====================
        warningStreamWithSelect.print("[select]登录失败告警");
        warningStreamWithProcess.print("[process]登录失败告警");

        // ==================== 9. 执行任务 ====================
        env.execute("Flink CEP 登录失败检测示例");
    }
}