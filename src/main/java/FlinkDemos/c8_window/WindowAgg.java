package FlinkDemos.c8_window;

import java.lang.Integer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.util.Collector;
import org.apache.commons.lang3.time.DateFormatUtils;

import FlinkDemos.beans.WaterSensor;

/**
 * 演示Flink窗口函数的使用
 */
public class WindowAgg {
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
        KeyedStream<WaterSensor, String> sensorKS = sensorDS.keyBy(WaterSensor::getId);
        // 这里以计数窗口为例
        WindowedStream<WaterSensor, String, GlobalWindow> sensorCountStream = sensorKS.countWindow(2);
        /**
         * 窗口函数是作用于每个窗口内的数据的处理逻辑，一般分为3个大类：
         * 1. 增量聚合函数：有 reduce 和 aggregate 两个api，主要用于 逐个处理窗口中的数据的聚合逻辑 —— 每一条数据都会触发一次窗口函数执行
         * 2. 全窗口函数：有 apply 和 process 两个 api，主要是应用于 整个窗口内部数据的聚合逻辑 —— 窗口所有数据到齐之后才会触发窗口函数执行
         * 3. 上述两者的结合
         */

        // ------------ 1. 增量聚合函数 ----------------
        /**
         * reduce：
         * 1、相同Key的第一条数据来的时候，不会调用reduce方法
         * 2、增量聚合：每个Key内部，来一条数据，就会计算一次，但是不会输出
         * 3、在窗口触发的时候，才会输出窗口的最终聚合结果
         * 返回的类型必须和原来的一样 —— 这是 reduce 算子的语义，也是最大的限制
         */
        SingleOutputStreamOperator<WaterSensor> reducedStream = sensorCountStream.reduce(
            // 传入一个实现 ReduceFunction 接口的匿名实现类
            new ReduceFunction<WaterSensor>() {
                @Override
                public WaterSensor reduce(WaterSensor value1, WaterSensor value2) throws Exception {
                    System.out.println("调用reduce方法，value1=" + value1 + ",value2=" + value2);
                    return new WaterSensor(value1.getId() + "+" + value2.getId(), value2.getTs(), value1.getVc() + value2.getVc());
                }
            }
        );
        //reducedStream.print("reducedStream");

        // aggregate 聚合，返回的类型可以不一样
        SingleOutputStreamOperator<String> aggregatedStream = sensorCountStream.aggregate(new MyAggregateFunction());
        //aggregatedStream.print("aggregatedStream");

        // ------------ 2. 全窗口函数 ----------------
        // 全窗口函数需要先收集整个窗口中的数据，并在内部缓存起来，等到窗口要输出结果的时候再取出数据进行计算
        // 全窗口函数也有两个：WindowFunction（对应于 .apply()） 和 ProcessWindowFunction（对应于 .process），
        // 但是前者是旧版本的接口，后面可能会被废弃，所以这里只介绍 ProcessWindowFunction 和对应的 .process
        SingleOutputStreamOperator<String> processStream = sensorCountStream.process(new MyProcessFunction());
        //processStream.print("processStream");

        // ------------ 3. 聚合函数 + 全窗口函数 ----------------
        // 聚合函数的 reduce 和 aggregate 方法里，还可以传入 ProcessWindowFunction子类，达到两者结合的目的
        // 此时的处理逻辑是：先用 聚合函数 逐条处理窗口里的数据，之后基于增量聚合结果，再用 全窗口函数 进行计算
        SingleOutputStreamOperator<String> compositeStream = sensorCountStream.aggregate(
            new MyAggregateFunction(),
            new MyProcessFunctionForAgg()
        );
        compositeStream.print("compositeStream");

        env.execute();
    }

}

/**
 * AggregateFunction 接口实现类
 * <WaterSensor>： 输入数据的类型
 * <Integer>：累加器的类型，存储的中间计算结果的类型
 * <String>：输出的类型
 * 使用逻辑：
 * 1、属于本窗口的第一条数据来，创建窗口，初始化累加器
 * 2、增量聚合：来一条计算一条，调用一次add方法
 * 3、窗口输出时调用一次 getResult 方法
 * 4、输入类型、中间累加器类型、输出类型可以不一样，非常灵活
 */
class MyAggregateFunction implements AggregateFunction<WaterSensor, Integer, String>{
    // 上面的类名后面不要跟泛型参数 ----- KEY
    //创建累加器，初始化累加器
    @Override
    public Integer createAccumulator() {
        Integer accumulator = 0;
        System.out.println("[MyAggregateFunction] -> createAccumulator: init accumulator=" + accumulator);
        return 0;
    }
    // 聚合逻辑
    @Override
    public Integer add(WaterSensor currentRecord, Integer accumulator) {
        Integer result = accumulator + currentRecord.getVc();
        System.out.println("[MyAggregateFunction] -> add, currentRecord=" + currentRecord
                + ", accumulator=" + accumulator.toString()
                + ", return: " + result);
        return result;
    }
    // 获取最终结果，窗口触发时输出
    @Override
    public String getResult(Integer accumulator) {
        String result = accumulator.toString();
        System.out.println("[MyAggregateFunction] -> getResult, return: " + result);
        return result;
    }
    // 只有会话窗口才会用到这个方法
    @Override
    public Integer merge(Integer integer, Integer acc1) {
        // 只有会话窗口才会用到
        System.out.println("[MyAggregateFunction] -> merge");
        return null;
    }
}

/**
 * ProcessWindowFunction 抽象类 —— 不是接口了
 * <WaterSensor>： 输入数据的类型
 * <String>：输出数据的类型
 * <String>：key的类型
 * <GlobalWindow>：Window类型
 * 使用逻辑：
 */
class MyProcessFunction extends ProcessWindowFunction<WaterSensor, String, String, GlobalWindow> {
    /**
     * 抽象方法 的参数
     * @param key The key for which this window is evaluated.
     * @param context The context in which the window is being evaluated. 注意，Context 是 抽象类 Window 的内部类.
     * @param elements The elements in the window being evaluated.
     * @param out A collector for emitting elements.
     * @throws Exception
     */
    @Override
    public void process(
        String key,
        ProcessWindowFunction<WaterSensor, String, String, GlobalWindow>.Context context,
        Iterable<WaterSensor> elements,
        Collector<String> out
    ) throws Exception {
        // 上下文可以拿到window对象，还有其他东西：侧输出流 等等
        long windowTs = context.window().maxTimestamp();
        String windowTsStr = DateFormatUtils.format(windowTs, "yyyy-MM-dd HH:mm:ss.SSS");
        long count = elements.spliterator().estimateSize();
        out.collect(
            "[MyProcessFunction] -> key=" + key
            + "的窗口@[" + windowTsStr + "]包含 "
            + count + " 条数据 ===> "
            + elements.toString()
        );
    }
}

/**
 * 和 MyAggregateFunction 聚合函数一起使用的 全窗口函数，它的第一个泛型不再是 WaterSensor了，而是聚合函数的输出
 */
class MyProcessFunctionForAgg extends ProcessWindowFunction<String, String, String, GlobalWindow> {
    /**
     * 抽象方法 的参数
     * @param key The key for which this window is evaluated.
     * @param context The context in which the window is being evaluated. 注意，Context 是 抽象类 Window 的内部变量.
     * @param elements The elements in the window being evaluated.
     * @param out A collector for emitting elements.
     * @throws Exception
     */
    @Override
    public void process(
        String key,
        ProcessWindowFunction<String, String, String, GlobalWindow>.Context context,
        Iterable<String> elements,
        Collector<String> out
    ) throws Exception {
        // 上下文可以拿到window对象，还有其他东西：侧输出流 等等
        long windowTs = context.window().maxTimestamp();
        String windowTsStr = DateFormatUtils.format(windowTs, "yyyy-MM-dd HH:mm:ss.SSS");
        // 查看当前 Key 的窗口数据条数，实际上只有一条，因为这里拿到的是 MyAggregateFunction 增量聚合的最终结果，每个Key只有一条记录
        long count = elements.spliterator().estimateSize();
        out.collect("[MyProcessFunctionForAgg] -> key=" + key
                + "的窗口@[" + windowTsStr + "]包含 "
                + count + " 条数据 ===> "
                + elements.toString());
    }
}

