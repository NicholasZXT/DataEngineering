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

public class WatermarkBasic {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 从集合中创建源数据
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
                new WaterSensor("s1", 1.0, 1),
                new WaterSensor("s1", 11.0, 11),
                new WaterSensor("s2", 2.0, 2),
                new WaterSensor("s3", 3.0, 3)
        );

        // WatermarkStrategy 是一个定义水位线生成方式的接口，它继承了两个接口：
        //  (1) TimestampAssignerSupplier<T>: createTimestampAssigner() 方法负责从流数据的某个字段中提取时间戳，作为水位线的依据
        //  (2) WatermarkGeneratorSupplier<T>: createWatermarkGenerator() 方法负责根据时间戳来生成水位线
        // 此接口里有一些静态方法可供调用，来配置生成对应的 WatermarkStrategy 接口实现类，不过调用顺序需要注意：
        // 必须要先设置水位线生成方式，再设置时间戳提取器
        // 1. 定义 watermark 生成策略
        WatermarkStrategy<WaterSensor> watermarkStrategy = WatermarkStrategy
                //1.1 指定watermark生成方式：
                //.noWatermarks()  // 无水位线
                //.forBoundedOutOfOrderness()  // 无序流水位线
                // 单调升序的watermark，没有等待时间 —— 只适用于有序流
                .<WaterSensor>forMonotonousTimestamps()
                // 1.2 指定 时间戳分配器，从数据中提取
                .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                    /**
                     * extractTimestamp 接口参数
                     * @param element The element that the timestamp will be assigned to.
                     * @param recordTimestamp The current internal timestamp of the element,
                     *                        or a negative value, if no timestamp has been assigned yet.
                     * @return
                     */
                    @Override
                    public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                        //返回的时间戳，要 毫秒
                        System.out.println("[extractTimestamp] element=" + element + ", recordTs@" + recordTimestamp);
                        System.out.println("element.ts=" + element.getTs().longValue());
                        long ts = element.getTs().longValue() + recordTimestamp;
                        System.out.println("ts=" + ts);
                        return ts;
                    }
                });

        // 2. 指定 watermark 策略
        SingleOutputStreamOperator<WaterSensor> sensorDSwithWM = sensorDS.assignTimestampsAndWatermarks(watermarkStrategy);

        // 3. 根据 watermark 策略，来定义对应的 事件时间语义 的窗口
        SingleOutputStreamOperator<String> result = sensorDSwithWM
                .keyBy(WaterSensor::getId)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
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
