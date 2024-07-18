package FlinkDemos.C9_watermark;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import FlinkDemos.beans.WaterSensor;

/**
 * WatermarkStrategy 是一个定义水位线生成方式的接口，它继承了两个接口：
 * (1) TimestampAssignerSupplier<T>: createTimestampAssigner() 方法负责从流数据的某个字段中提取时间戳，作为水位线的依据
 * (2) WatermarkGeneratorSupplier<T>: createWatermarkGenerator() 方法负责根据时间戳来生成水位线
 * 此接口里的方法分成 3 个部分：
 * (1) 接口实现类必须要实现的方法
 * (2) 基于Base Strategy 来构建 WatermarkStrategy 的 builder methods
 * (3) 提供了一些创建内置水位线策略的快捷方法
 * 其中第 3 部分的快捷方法（都是静态方法）如下：
 *  noWatermarks(): 不创建水位线
 *  forMonotonousTimestamps(): 创建单调递增水位线
 *  forBoundedOutOfOrderness(): 乱序流水位线
 *  forGenerator()
 *  前 3 个方法比较常用，它们在设置水位线生成方式的同时会返回一个 WatermarkStrategy 接口实现类（似乎不是？），之后再设置时间戳提取器
 */
public class WatermarkBasic {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从集合中创建源数据，有序流
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
                new WaterSensor("s1", 1.0, 10),
                new WaterSensor("s2", 2.0, 20),
                new WaterSensor("s1", 3.0, 30),
                new WaterSensor("s2", 4.0, 40),
                new WaterSensor("s1", 5.0, 50),
                new WaterSensor("s2", 6.0, 60)
        );

        // 1. 定义 watermark 生成策略
        WatermarkStrategy<WaterSensor> watermarkStrategy = WatermarkStrategy
                // 单调升序的watermark，没有等待时间 —— 只适用于有序流
                .<WaterSensor>forMonotonousTimestamps()
                // 1.2 指定 时间戳分配器，从数据中提取
                .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                    /**
                     * extractTimestamp 接口参数
                     * @param element The element that the timestamp will be assigned to.
                     * @param recordTimestamp The current internal timestamp of the element, or a negative value, if no timestamp has been assigned yet.
                     * @return The new timestamp
                     */
                    @Override
                    public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                        //返回的时间戳，单位是 毫秒
                        System.out.println("[extractTimestamp] element=" + element + ", recordTs@" + recordTimestamp);
                        System.out.println("element.ts=" + element.getTs().longValue());
                        // 秒转毫秒
                        return element.getTs().longValue() * 1000L;
                    }
                });

        // 2. 指定 watermark 策略
        SingleOutputStreamOperator<WaterSensor> sensorDSwithWM = sensorDS.assignTimestampsAndWatermarks(watermarkStrategy);

        // 3. 根据 watermark 策略，来定义对应的 事件时间语义 的窗口
        SingleOutputStreamOperator<String> result = sensorDSwithWM
                .keyBy(WaterSensor::getId)
                // 使用 滚动时间窗口
                .window(TumblingEventTimeWindows.of(Time.seconds(2)))
                .process(
                        new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                             @Override
                             public void process(String key,
                                                 ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context,
                                                 Iterable<WaterSensor> elements,
                                                 Collector<String> out) throws Exception {
                                 long startTs = context.window().getStart();
                                 long endTs = context.window().getEnd();
                                 String windowStart = DateFormatUtils.format(startTs, "yyyy-MM-dd HH:mm:ss");
                                 String windowEnd = DateFormatUtils.format(endTs, "yyyy-MM-dd HH:mm:ss");
                                 long count = elements.spliterator().estimateSize();
                                 out.collect("key=" + key + "的窗口@[" + windowStart + ", " +
                                         windowEnd + "]包含 " +
                                         count + " 条数据 ===> " +
                                         elements.toString());
                             }
                         }
                );

        result.print("result");

        env.execute();

    }
}

class MyProcessWindowFunction extends ProcessWindowFunction<WaterSensor, String, String, TimeWindow>{
    @Override
    public void process(String s,
                        ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context,
                        Iterable<WaterSensor> elements,
                        Collector<String> out) throws Exception {

    }
}
