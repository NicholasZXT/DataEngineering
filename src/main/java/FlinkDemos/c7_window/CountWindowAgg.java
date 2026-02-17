package FlinkDemos.c7_window;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.commons.lang3.time.DateFormatUtils;
import FlinkDemos.beans.WaterSensor;
import org.apache.flink.util.Collector;

/**
 * 计数窗口练习
 */
public class CountWindowAgg {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        // 从集合中创建源数据
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
            new WaterSensor("s1", 1.0, 1),
            new WaterSensor("s2", 2.0, 2),
            new WaterSensor("s1", 3.0, 3),
            new WaterSensor("s2", 4.0, 4),
            new WaterSensor("s1", 5.0, 5),
            new WaterSensor("s2", 6.0, 6),
            new WaterSensor("s1", 7.0, 7),
            new WaterSensor("s2", 8.0, 8)
        );
        // 1. 先分组
        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(WaterSensor::getId);
        // 2. 设置计数窗口，每 2 个数据 一个窗口，注意，不足 2 条数据的窗口，不会输出
        WindowedStream<WaterSensor, String, GlobalWindow> sensorCWS1 = sensorKS.countWindow(2);
        // 3. 设置窗口函数
        SingleOutputStreamOperator<String> process1 = sensorCWS1.process(
            // 这里使用的是 ProcessWindowFunction 接口
            new ProcessWindowFunction<WaterSensor, String, String, GlobalWindow>() {
                /**
                 * 全窗口函数计算逻辑：窗口触发时才会调用一次，统一计算窗口的所有数据
                 * @param key 分组的key
                 * @param context 上下文
                 * @param elements 存的数据
                 * @param out 采集器
                 * @throws Exception
                 */
                @Override
                public void process(
                    String key,
                    ProcessWindowFunction<WaterSensor, String, String, GlobalWindow>.Context context,
                    Iterable<WaterSensor> elements,
                    Collector<String> out
                ) throws Exception {
                    // 这里拿到的窗口类型是 GlobalWindow
                    String windowType = context.window().toString();
                    long maxTs = context.window().maxTimestamp(); // 这个值就是 Long 的最大值，这里没啥用
                    String maxTime = DateFormatUtils.format(maxTs, "yyyy-MM-dd HH:mm:ss.SSS");
                    long count = elements.spliterator().estimateSize();
                    out.collect("key: " + key + ", windowType: " + windowType + ", count: " + count + ", data: " + elements);
                }
            }
        );
        process1.print("CountWindowStream");
        // 输出如下，可以看出 2个一个窗口，输出2次
        //CountWindowStream:2> key: s1, windowType: GlobalWindow, count: 2, data: [WaterSensor{id='s1', ts=1.0, vc=1}, WaterSensor{id='s1', ts=3.0, vc=3}]
        //CountWindowStream:1> key: s2, windowType: GlobalWindow, count: 2, data: [WaterSensor{id='s2', ts=2.0, vc=2}, WaterSensor{id='s2', ts=4.0, vc=4}]
        //CountWindowStream:2> key: s1, windowType: GlobalWindow, count: 2, data: [WaterSensor{id='s1', ts=5.0, vc=5}, WaterSensor{id='s1', ts=7.0, vc=7}]
        //CountWindowStream:1> key: s2, windowType: GlobalWindow, count: 2, data: [WaterSensor{id='s2', ts=6.0, vc=6}, WaterSensor{id='s2', ts=8.0, vc=8}]

        // 计数窗口，每 4 个数据 一个窗口，滑动步长 slide=2
        WindowedStream<WaterSensor, String, GlobalWindow> sensorCWS2 = sensorKS.countWindow(4,2);
        SingleOutputStreamOperator<String> process2 = sensorCWS2.process(
            new ProcessWindowFunction<WaterSensor, String, String, GlobalWindow>() {
                @Override
                public void process(
                    String key,
                    ProcessWindowFunction<WaterSensor, String, String, GlobalWindow>.Context context,
                    Iterable<WaterSensor> elements,
                    Collector<String> out
                ) throws Exception {
                    long maxTs = context.window().maxTimestamp(); // 这个值就是 Long 的最大值，这里没啥用
                    String maxTime = DateFormatUtils.format(maxTs, "yyyy-MM-dd HH:mm:ss.SSS");
                    String windowType = context.window().toString();
                    long count = elements.spliterator().estimateSize();
                    out.collect("key: " + key + ", windowType: " + windowType + ", count: " + count + ", data: " + elements);
                }
            }
        );
        process2.print("CountWindowStreamSlide");
        // 输出如下： 窗口size=4，slide=2，每个key 输出了 2 次，第一次是在 slide=2 时输出，第二次才是输出整个窗口的数据
        //CountWindowStreamSlide:1> key: s2, windowType: GlobalWindow, count: 2, data: [WaterSensor{id='s2', ts=2.0, vc=2}, WaterSensor{id='s2', ts=4.0, vc=4}]
        //CountWindowStreamSlide:2> key: s1, windowType: GlobalWindow, count: 2, data: [WaterSensor{id='s1', ts=1.0, vc=1}, WaterSensor{id='s1', ts=3.0, vc=3}]
        //CountWindowStreamSlide:1> key: s2, windowType: GlobalWindow, count: 4, data: [WaterSensor{id='s2', ts=2.0, vc=2}, WaterSensor{id='s2', ts=4.0, vc=4}, WaterSensor{id='s2', ts=6.0, vc=6}, WaterSensor{id='s2', ts=8.0, vc=8}]
        //CountWindowStreamSlide:2> key: s1, windowType: GlobalWindow, count: 4, data: [WaterSensor{id='s1', ts=1.0, vc=1}, WaterSensor{id='s1', ts=3.0, vc=3}, WaterSensor{id='s1', ts=5.0, vc=5}, WaterSensor{id='s1', ts=7.0, vc=7}]

        env.execute();
    }
}
