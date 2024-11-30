package FlinkDemos.c8_window;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import FlinkDemos.beans.WaterSensor;
import org.apache.flink.util.Collector;

public class TimeWindowAgg {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
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
            .map(new WaterSensorMapFunction());

        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(WaterSensor::getId);

        // 滚动时间窗口
        WindowedStream<WaterSensor, String, TimeWindow> sensorTWS1 = sensorKS.window(TumblingProcessingTimeWindows.of(Time.seconds(5)));
        SingleOutputStreamOperator<String> process1 = sensorTWS1.process(
            new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                @Override
                public void process(
                    String key,
                    ProcessWindowFunction<WaterSensor, String, String, TimeWindow>.Context context,
                    Iterable<WaterSensor> elements,
                    Collector<String> out
                ) throws Exception {
                    // 这里拿到的窗口类型是 ProcessWindow
                    String windowType = context.window().toString();
                    long startTs = context.window().getStart();
                    long endTs = context.window().getEnd();
                    long count = elements.spliterator().estimateSize();
                    String windowStart = DateFormatUtils.format(startTs, "yyyy-MM-dd HH:mm:ss.SSS");
                    String windowEnd = DateFormatUtils.format(endTs, "yyyy-MM-dd HH:mm:ss.SSS");
                    String res = "key: " + key + ", windowType: " + windowType +
                        ", WindowTimeRange: [" + windowStart + ", " + windowEnd + "], count: " + count +
                        ", data: " + elements;
                    out.collect(res);
                }
            }
        );
        process1.print("TumblingProcessingStream");
        env.execute();

    }

    public static class WaterSensorMapFunction implements MapFunction<String, WaterSensor>{
        @Override
        public WaterSensor map(String value) throws Exception {
            String[] datas = value.split(",");
            return new WaterSensor(datas[0], Double.valueOf(datas[1]), Integer.valueOf(datas[2]));
        }
    }
}
