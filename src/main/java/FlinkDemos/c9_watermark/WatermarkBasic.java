package FlinkDemos.c9_watermark;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;  // 水位线策略接口
import org.apache.flink.api.common.eventtime.TimestampAssigner;  // 所有时间戳提取方法底层对应的接口
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;  // Lambda时间戳提取接口
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier;      // 支持上下文的时间戳提取工厂接口
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import java.time.Duration;
import FlinkDemos.beans.WaterSensor;
import FlinkDemos.c8_window.TimeWindowAgg;

/**
 * WatermarkStrategy 是一个定义水位线生成方式的接口，它继承了两个接口：
 * (1) TimestampAssignerSupplier<T>: createTimestampAssigner() 方法负责从流数据的某个字段中提取时间戳，作为水位线的依据
 * (2) WatermarkGeneratorSupplier<T>: createWatermarkGenerator() 方法负责根据时间戳来生成水位线
 * 准确来说，此接口定义的是一个生成 WatermarkGenerator 接口实现类的 builder/factory。
 * 此接口里的方法分成 3 个部分：
 * (1) TimestampAssignerSupplier 和 WatermarkGeneratorSupplier 接口必须要实现的方法
 * (2) 基于 Base Strategy 来构建 WatermarkStrategy 的 builder methods，这些method描述符均为 default
 * (3) 提供了一些创建内置水位线策略的快捷方法——均为静态方法
 * 其中第 3 部分的快捷方法（都是静态方法）如下：
 *  noWatermarks(): 不创建水位线
 *  forMonotonousTimestamps(): 创建单调递增水位线
 *  forBoundedOutOfOrderness(): 乱序流水位线
 *  forGenerator()
 *  前 3 个方法比较常用，它们在设置水位线生成方式的同时会返回一个 WatermarkGenerator 接口实现类，之后再设置时间戳提取器
 */
public class WatermarkBasic {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从集合中创建源数据，有序流
        //DataStreamSource<WaterSensor> sensorDS = env.fromElements(
        //    new WaterSensor("s1", 1.0, 10),
        //    new WaterSensor("s2", 2.0, 20),
        //    new WaterSensor("s1", 3.0, 30),
        //    new WaterSensor("s2", 4.0, 40),
        //    new WaterSensor("s1", 5.0, 50),
        //    new WaterSensor("s2", 6.0, 60)
        //);
        // 这个演示最好使用 socket 流
        SingleOutputStreamOperator<WaterSensor> sensorDS = env
            .socketTextStream("localhost", 7890)
            .map(new TimeWindowAgg.WaterSensorMapFunction());

        // 1. 定义 watermark 生成策略
        // 1.1 定义有序流的单调递增水位线生成策略
        WatermarkStrategy<WaterSensor> watermarkStrategy = WatermarkStrategy
            // 单调升序的watermark，没有等待时间 —— 只适用于有序流
            .<WaterSensor>forMonotonousTimestamps()
            // 指定 时间戳分配器，从数据中提取时间戳
            .withTimestampAssigner(
                // 这里使用接口匿名实现类，比较繁琐
                new SerializableTimestampAssigner<WaterSensor>() {
                    @Override
                    public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                        //返回的时间戳，单位是 毫秒
                        System.out.println("[extractTimestamp] element=" + element + ", recordTs@" + recordTimestamp);
                        System.out.println("element.ts=" + element.getTs().longValue());
                        // 秒转毫秒
                        return element.getTs().longValue() * 1000L;
                    }
                }
            );

        // 1.2 定义乱序流的水位线生成策略
        WatermarkStrategy<WaterSensor> watermarkStrategyOutOfOrder = WatermarkStrategy
            // 指定watermark生成：乱序的，等待3s
            .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3))
            // 指定 时间戳分配器，从数据中提取时间戳
            .withTimestampAssigner(
                // 这里使用 Lambda 表达式，更加简洁
                (element, recordTimestamp) -> {
                    // 返回的时间戳，要求是毫秒
                    System.out.println("数据=" + element + ",recordTs=" + recordTimestamp);
                    return element.getTs().longValue() * 1000L;
                });

        // 2. 指定 watermark 策略
        SingleOutputStreamOperator<WaterSensor> sensorDSWithWM = sensorDS.assignTimestampsAndWatermarks(watermarkStrategy);

        // 3. 根据 watermark 策略，来定义对应的 事件时间语义 的窗口
        SingleOutputStreamOperator<String> result = sensorDSWithWM
            .keyBy(WaterSensor::getId)
            // 使用 滚动时间窗口
            .window(TumblingEventTimeWindows.of(Time.seconds(2)))
            .process(
                new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                     @Override
                     public void process (
                         String key,
                         ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context,
                         Iterable<WaterSensor> elements,
                         Collector<String> out
                     ) throws Exception {
                         long startTs = context.window().getStart();
                         long endTs = context.window().getEnd();
                         String windowStart = DateFormatUtils.format(startTs, "yyyy-MM-dd HH:mm:ss");
                         String windowEnd = DateFormatUtils.format(endTs, "yyyy-MM-dd HH:mm:ss");
                         long count = elements.spliterator().estimateSize();
                         out.collect(
                             "key=" + key + "的窗口@[" + windowStart + ", " +
                             windowEnd + "]包含 " +
                             count + " 条数据 ===> " +
                             elements.toString()
                         );
                     }
                 }
            );

        result.print("result");

        env.execute();
    }
}
