package FlinkDemos.c9_watermark;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import FlinkDemos.beans.WaterSensor;
import FlinkDemos.c8_window.TimeWindowAgg;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * 演示自定义水位线生成器
 */
public class WatermarkCustom {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        // 默认周期 200ms
        env.getConfig().setAutoWatermarkInterval(2000);
        // 从集合中创建源数据
        //DataStreamSource<WaterSensor> sensorDS = env.fromElements(
        //    new WaterSensor("s1", 1.0, 1),
        //    new WaterSensor("s2", 2.0, 2),
        //    new WaterSensor("s1", 3.0, 3),
        //    new WaterSensor("s2", 4.0, 4),
        //    new WaterSensor("s1", 5.0, 5),
        //    new WaterSensor("s2", 6.0, 6),
        //    new WaterSensor("s1", 7.0, 7),
        //    new WaterSensor("s2", 8.0, 8)
        //);
        // 这个演示还必须要使用 socket 流
        SingleOutputStreamOperator<WaterSensor> sensorDS = env
            .socketTextStream("localhost", 7890)
            .map(new TimeWindowAgg.WaterSensorMapFunction());

        WatermarkStrategy<WaterSensor> watermarkStrategy = WatermarkStrategy
            // 指定 自定义的 生成器
            // 1.自定义的 周期性生成
             .<WaterSensor>forGenerator(ctx -> new MyPeriodWatermark<>(3000L))
            // 2.自定义的 断点式生成
            //.<WaterSensor>forGenerator(ctx -> new MyPuntuatedWatermark<>(3000L))
            .withTimestampAssigner(
                (element, recordTimestamp) -> {
                    return element.getTs().longValue() * 1000L;
                }
            );

        SingleOutputStreamOperator<WaterSensor> sensorDSwithWM = sensorDS.assignTimestampsAndWatermarks(watermarkStrategy);

        sensorDSwithWM
            .keyBy(WaterSensor::getId)
            // 使用滚动窗口
            .window(TumblingEventTimeWindows.of(Time.seconds(10)))
            .process(
                new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                    @Override
                    public void process(
                        String s, Context context, Iterable<WaterSensor> elements, Collector<String> out
                    ) throws Exception {
                        long startTs = context.window().getStart();
                        long endTs = context.window().getEnd();
                        String windowStart = DateFormatUtils.format(startTs, "yyyy-MM-dd HH:mm:ss.SSS");
                        String windowEnd = DateFormatUtils.format(endTs, "yyyy-MM-dd HH:mm:ss.SSS");

                        long count = elements.spliterator().estimateSize();
                        out.collect("key=" + s + "的窗口[" + windowStart + "," + windowEnd + ")包含" +
                            count + "条数据===>" + elements.toString()
                        );
                    }
                }
            )
            .print();

        env.execute();

    }
}

class MyPeriodWatermark<T> implements WatermarkGenerator<T> {
    // 乱序等待时间
    private long delayTs;
    // 用来保存 当前为止 最大的事件时间
    private long maxTs;

    public MyPeriodWatermark(long delayTs) {
        this.delayTs = delayTs;
        this.maxTs = Long.MIN_VALUE + this.delayTs + 1;
    }

    /**
     * 每条数据来，都会调用一次： 用来提取最大的事件时间，保存下来
     * @param event
     * @param eventTimestamp 提取到的数据的 事件时间
     * @param output
     */
    @Override
    public void onEvent(T event, long eventTimestamp, WatermarkOutput output) {
        maxTs = Math.max(maxTs, eventTimestamp);
        System.out.println("调用onEvent方法，获取目前为止的最大时间戳=" + maxTs);
    }

    /**
     * 周期性调用： 发射 watermark
     * @param output
     */
    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
        output.emitWatermark(new Watermark(maxTs - delayTs - 1));
        System.out.println("调用onPeriodicEmit方法，生成watermark=" + (maxTs - delayTs - 1));
    }
}

class MyPuntuatedWatermark<T> implements WatermarkGenerator<T> {
    // 乱序等待时间
    private long delayTs;
    // 用来保存 当前为止 最大的事件时间
    private long maxTs;

    public MyPuntuatedWatermark(long delayTs) {
        this.delayTs = delayTs;
        this.maxTs = Long.MIN_VALUE + this.delayTs + 1;
    }

    /**
     * 每条数据来，都会调用一次： 用来提取最大的事件时间，保存下来,并发射watermark
     * @param event
     * @param eventTimestamp 提取到的数据的 事件时间
     * @param output
     */
    @Override
    public void onEvent(T event, long eventTimestamp, WatermarkOutput output) {
        maxTs = Math.max(maxTs, eventTimestamp);
        output.emitWatermark(new Watermark(maxTs - delayTs - 1));
        System.out.println("调用onEvent方法，获取目前为止的最大时间戳=" + maxTs+",watermark="+(maxTs - delayTs - 1));
    }

    /**
     * 周期性调用： 不需要
     * @param output
     */
    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
    }
}
