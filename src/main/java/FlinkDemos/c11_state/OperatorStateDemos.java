package FlinkDemos.c11_state;

import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import java.time.Duration;

import FlinkDemos.beans.WaterSensor;
import FlinkDemos.c8_window.TimeWindowAgg;
import org.apache.flink.util.Collector;

/**
 * 演示 算子状态
 */
public class OperatorStateDemos {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
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
        // 这个演示建议使用 socket 流
        SingleOutputStreamOperator<String> sensorDS = env
            .socketTextStream("localhost", 7890);
            //.map(new TimeWindowAgg.WaterSensorMapFunction());
        // 算子状态不需要进行分区

        // ---------------- ListState 使用 ----------------
        // 在map算子中计算数据的个数
        sensorDS.map(new MyCountMapFunction()).print();

        // ---------------- BroadcastState 使用 ----------------
        // 水位超过指定的阈值发送告警，阈值可以动态修改
        // 配置流（用来广播配置）
        DataStreamSource<String> configDS = env.socketTextStream("localhost", 7898);
        // 1. 将 配置流 广播
        MapStateDescriptor<String, Integer> broadcastMapState = new MapStateDescriptor<>(
            "broadcast-state", Types.STRING, Types.INT
        );
        BroadcastStream<String> configBS = configDS.broadcast(broadcastMapState);
        // 2.把 数据流 和 广播后的配置流 connect
        BroadcastConnectedStream<WaterSensor, String> sensorBCS = sensorDS
            .map(new TimeWindowAgg.WaterSensorMapFunction())
            .connect(configBS);
        sensorBCS.process(new MyBroadcastFunction(broadcastMapState)).print();

        env.execute();
    }


    // 实现 MapFunction + CheckpointedFunction 接口
    public static class MyCountMapFunction implements MapFunction<String, Long>, CheckpointedFunction {

        private Long count = 0L;
        private ListState<Long> state;

        @Override
        public Long map(String value) throws Exception {
            return ++count;
        }

        /**
         * 2.本地变量持久化：将 本地变量 拷贝到 算子状态中,开启checkpoint时才会调用
         * @param context
         * @throws Exception
         */
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            System.out.println("snapshotState...");
            // 2.1 清空算子状态
            state.clear();
            // 2.2 将 本地变量 添加到 算子状态 中
            state.add(count);
        }

        /**
         * 3.初始化本地变量：程序启动和恢复时，从状态中把数据添加到本地变量，每个子任务调用一次
         * @param context
         * @throws Exception
         */
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            System.out.println("initializeState...");
            // 3.1 从 上下文 初始化 算子状态
            // list 与 unionlist 的区别：并行度改变时，怎么重新分配状态
            //   list：轮询均分 给 新的 并行子任务
            //   unionlist状态：原先的多个子任务的状态，合并成一份完整的。会把 完整的列表 广播给新的并行子任务（每人一份完整的）
            state = context
                .getOperatorStateStore()
                .getListState(new ListStateDescriptor<Long>("state", Types.LONG));
            //.getUnionListState(new ListStateDescriptor<Long>("union-state", Types.LONG));
            // 3.2 从 算子状态中 把数据 拷贝到 本地变量
            if (context.isRestored()) {
                for (Long c : state.get()) {
                    count += c;
                }
            }
        }
    }

    public static class MyBroadcastFunction extends BroadcastProcessFunction<WaterSensor, String, String> {

        private final MapStateDescriptor<String, Integer> broadcastMapState;

        public MyBroadcastFunction(MapStateDescriptor<String, Integer> broadcastMapState){
            this.broadcastMapState = broadcastMapState;
        }
        
        /**
         * 数据流的处理方法： 数据流 只能 读取 广播状态，不能修改
         * @param value
         * @param ctx
         * @param out
         * @throws Exception
         */
        @Override
        public void processElement(
            WaterSensor value, ReadOnlyContext ctx, Collector<String> out
        ) throws Exception {
            // 通过上下文获取广播状态，取出里面的值（只读，不能修改）
            ReadOnlyBroadcastState<String, Integer> broadcastState = ctx.getBroadcastState(broadcastMapState);
            Integer threshold = broadcastState.get("threshold");
            // 判断广播状态里是否有数据，因为刚启动时，可能是数据流的第一条数据先来
            threshold = (threshold == null ? 0 : threshold);
            if (value.getVc() > threshold) {
                out.collect(value + ",水位超过指定的阈值：" + threshold + "!!!");
            }
        }

        /**
         * 广播后的配置流的处理方法:  只有广播流才能修改 广播状态
         * @param value
         * @param ctx
         * @param out
         * @throws Exception
         */
        @Override
        public void processBroadcastElement(
            String value, Context ctx, Collector<String> out
        ) throws Exception {
            // 通过上下文获取广播状态，往里面写数据
            BroadcastState<String, Integer> broadcastState = ctx.getBroadcastState(broadcastMapState);
            broadcastState.put("threshold", Integer.valueOf(value));
        }
    }

}
