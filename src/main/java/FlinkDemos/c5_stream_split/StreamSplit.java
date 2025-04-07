package FlinkDemos.c5_stream_split;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.configuration.Configuration;
import FlinkDemos.beans.WaterSensor;

/**
 * 分流和侧流操作
 */
public class StreamSplit {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 从集合中创建源数据
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
            new WaterSensor("s1", 1.0, 1),
            new WaterSensor("s2", 11.0, 2),
            new WaterSensor("s3", 2.0, 3),
            new WaterSensor("s4", 3.0, 4),
            new WaterSensor("s5", 3.0, 0),
            new WaterSensor("s6", 3.0, -1)
        );

        // 使用过滤的方式分成 奇数流 和 偶数流，这个方式要重复使用
        //SingleOutputStreamOperator<WaterSensor> even = sensorDS.filter(sensor -> sensor.getVc() % 2 == 0);
        //SingleOutputStreamOperator<WaterSensor> odd = sensorDS.filter(sensor -> sensor.getVc() % 2 == 1);

        // 使用侧流的方式过滤，步骤如下：
        //1、定义 OutputTag 对象
        //2、使用 process 算子
        //3、调用 ctx.output
        //4、通过主流 获取 测流

        // 创建OutputTag对象，第一个参数：标签名；第二个参数：放入侧输出流中的 数据的 类型——使用Types申明
        OutputTag<WaterSensor> evenTag = new OutputTag<>("even", Types.POJO(WaterSensor.class));
        OutputTag<WaterSensor> oddTag = new OutputTag<>("odd", Types.POJO(WaterSensor.class));

        // 调用process方法
        SingleOutputStreamOperator<WaterSensor> processStream = sensorDS.process(new SplitProcessFunction(evenTag, oddTag));

        // 从主流中，根据标签 获取 侧输出流
        SideOutputDataStream<WaterSensor> evenStream = processStream.getSideOutput(evenTag);
        SideOutputDataStream<WaterSensor> oddStream = processStream.getSideOutput(oddTag);

        // 输出主流
        processStream.print("MainStream");
        // 输出侧流
        evenStream.print("EvenStream");
        oddStream.print("OddStream");

        env.execute();
    }
}

class SplitProcessFunction extends ProcessFunction<WaterSensor, WaterSensor> {
    private OutputTag<WaterSensor> evenTag;
    private OutputTag<WaterSensor> oddTag;

    public SplitProcessFunction(OutputTag<WaterSensor> evenTag, OutputTag<WaterSensor> oddTag){
        this.evenTag = evenTag;
        this.oddTag = oddTag;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        // Tag 的定义最好不要放在 open 方法里，更不能放在 processElement 方法里
        //evenTag = new OutputTag<>("even", Types.POJO(WaterSensor.class));
        //oddTag = new OutputTag<>("odd", Types.POJO(WaterSensor.class));
        System.out.println("open method called with tags: " + evenTag + ", " + oddTag);
    }

    @Override
    public void processElement(
        WaterSensor value,
        ProcessFunction<WaterSensor, WaterSensor>.Context ctx,
        Collector<WaterSensor> out
    ) throws Exception {
        int vc = value.getVc();
        if (vc <= 0){
            // 输出到主流
            out.collect(value);
        }else if (vc % 2 == 0){
            // 使用 Context 对象输出到侧流，第一个参数是 Tag, 第二个参数是数据
            ctx.output(evenTag, value);
        }else {
            ctx.output(oddTag, value);
        }
    }
}
