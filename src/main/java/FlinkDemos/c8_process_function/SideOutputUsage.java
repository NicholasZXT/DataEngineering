package FlinkDemos.c8_process_function;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

/**
 * 侧输出流使用示例：根据数字奇偶性分流到不同侧输出流
 */
public class SideOutputUsage {

    // 必须是 static final，并使用匿名内部类 {} 以保留泛型信息
    private static final OutputTag<String> EVEN_OUTPUT_TAG = new OutputTag<String>("even-numbers") {
    };

    private static final OutputTag<String> ODD_OUTPUT_TAG = new OutputTag<String>("odd-numbers") {
    };

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1); // 设置并行度为1，便于观察输出顺序

        // 输入数据：字符串形式的数字
        SingleOutputStreamOperator<String> processedStream =
                env.fromElements("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                        .process(new NumberClassifierProcessFunction());

        // 主输出流（本例中不使用，留空）
        processedStream.print("MAIN");

        // 也有使用如下方式来定义 OutputTag 的，但是还是推荐使用上面匿名类的方式。
        //OutputTag<String> evenTag = new OutputTag<>("even", Types.STRING);
        //OutputTag<String> oddTag = new OutputTag<>("odd", Types.STRING);

        // 获取两个侧输出流
        processedStream.getSideOutput(EVEN_OUTPUT_TAG).print("EVEN");
        processedStream.getSideOutput(ODD_OUTPUT_TAG).print("ODD");

        // 执行作业
        env.execute("Side Output Example - Odd/Even Split");
    }

    /**
     * 自定义 ProcessFunction：根据数字奇偶性分流到不同侧输出流
     */
    public static class NumberClassifierProcessFunction extends ProcessFunction<String, String> {

        @Override
        public void processElement(
                String value,
                ProcessFunction<String, String>.Context ctx,
                Collector<String> out
        ) throws Exception {
            try {
                int number = Integer.parseInt(value.trim());
                if (number % 2 == 0) {
                    // 偶数 → 发送到 EVEN 侧输出流
                    ctx.output(EVEN_OUTPUT_TAG, value);
                } else {
                    // 奇数 → 发送到 ODD 侧输出流
                    ctx.output(ODD_OUTPUT_TAG, value);
                }
                // 注意：主输出流（out.collect()）未被使用
            } catch (NumberFormatException e) {
                // 可选：添加第三个侧输出流处理非法输入
                // ctx.output(INVALID_TAG, value);
                System.err.println("Invalid number: " + value);
            }
        }
    }
}
