package FlinkDemos;

import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * 演示Flink窗口分配函数的使用
 */
public class WindowApi {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 从集合中创建源数据
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
                new WaterSensor("s1", 1.0, 1),
                new WaterSensor("s1", 11.0, 11),
                new WaterSensor("s2", 2.0, 2),
                new WaterSensor("s3", 3.0, 3)
        );

        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(WaterSensor::getId);

        // 窗口有 全局窗口（不分区） 和 分区窗口（按键分区）
        // 全局窗口是直接在整个stream上进行窗口操作——此时并行度只能是1；
        //sensorDS.windowAll();
        // 分区窗口是 分区之后，每个分区各自有各自的窗口——此时可以多分区并行
        //sensorKS.window();
        // 实际中常用的是分区窗口，下面也以分区窗口为例进行练习

        // ------------ 第1步：指定窗口 ------------------
        // 窗口的类型如下：
        // 1. 基于计数的窗口，比较简单，只有 countWindow 一个api
        sensorKS.countWindow(5);  // 滚动窗口，窗口长度=5个元素
        sensorKS.countWindow(5,2); // 滑动窗口，窗口长度=5个元素，滑动步长=2个元素
        // 2. 基于时间的窗口，比较丰富，一般要通过 org.apache.flink.streaming.api.windowing.assigners.xxx 提供的窗口分配器来定义窗口
        sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(10))); // 滚动窗口，窗口长度10s
        sensorKS.window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(2))); // 滑动窗口，窗口长度10s，滑动步长2s
        sensorKS.window(ProcessingTimeSessionWindows.withGap(Time.seconds(5))); // 会话窗口，超时间隔5s

        // 全局窗口，计数窗口的底层就是用的这个，需要自定义的时候才会用
        //sensorKS.window(GlobalWindows.create());

        // 窗口函数返回的是一个 WindowedStream 类型：第1个泛型是流的数据类型，第2个泛型是key的类型，第3个泛型是窗口分配器的类型
        WindowedStream<WaterSensor, String, GlobalWindow> sensorCountStream = sensorKS.countWindow(5);
        WindowedStream<WaterSensor, String, TimeWindow> sensorTimeStream = sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(10)));

        // ------------ 第2步：指定窗口函数，也就是窗口内数据的计算逻辑 ------------------
        // 参见 WindowAgg 类
        //wsCStream.reduce();
        //wsCStream.aggregate();
        //wsCStream.apply();
        //wsCStream.process();

        env.execute();

    }
}
