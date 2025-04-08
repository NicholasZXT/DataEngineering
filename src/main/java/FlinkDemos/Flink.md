# Flink依赖包结构

## `org.apache.flink:flink-core`

包含了 Flink 的核心功能和基础设施，提供了一些基础类和接口。

此依赖package命令空间为`org.apache.flink`，**常用**的package划分如下：

`api`——最常用
+ **`common`**
    + **`functions`**，定义了Flink里所有函数式操作的接口/抽象基类
        + **`Function`**接口，所有函数式操作的基本接口，不过这个接口只是用于标识作用， 没有定义任何方法
        + `MapFunction<T, O> `接口
        + `FilterFunction<T>`接口
        + `FlatMapFunction<T, O>`接口
        + `ReduceFunction<T>`接口
        + `CombineFunction<IN, OUT>`接口
        + ` AggregateFunction<IN, ACC, OUT>`接口
        + **`RichFunction`**接口，所有富函数的基本接口
        + `AbstractRichFunction`抽象类，实现了`RichFunction`接口，所有富函数基本都要继承此抽象类
        + `RichMapFunction<IN, OUT>`抽象类
        + `RichFilterFunction<T>`抽象类
        + `RichReduceFunction<T>`抽象类
        + 还有其他的就不列举了
    + `eventtime`
        + `Watermark`类，这个也是数据类，和下面`org.apache.fling.streaming.watermark.Watermark`类很相似，不知道为啥这么设计
        + **`WatermarkStrategy<T>`**，水位线分配策略接口，里面也提供了常用水位线策略的默认方法实现
        + `SerializableTimestampAssigner<T>`接口，
    + `state`，状态相关的内容
        + `ValueState`接口，`ValueStateDescriptor`类
        + `ListState`接口，`ListStateDescriptor`类
        + `MapState`接口，`MapStatedDescriptor`类
        + 等等
    + `typeinfo`，Flink提供的类型提示信息，比如`BasicTypeInfo`，`TypeInformation`等
+ `java`，这里面好像没啥常用的东西
    + `functions`，没啥东西
    + `tuple`
    + `typeutils`

`types`

------
## `org.apache.flink:flink-streaming-java`

包含了用于构建和处理数据流的 API，提供了 `DataStream` 类及其相关操作（如 `map`, `filter`, `keyBy`, `process` 等）

此依赖的package命名空间为`org.apache.flink.streaming`，常用package划分如下：

`api`——最常用

+ `environment`，Flink应用程序的起点，有如下常用的类
    + `StreamExecutionEnvironment`，一般用这个就行了
    + `RemoteStreamEnvironment`
    + `LocalStreamEnvironment`
+ **`datastream`**，定义了DataStream基本的接口和类，常用的有如下接口/类
    + `DataStreamSink`类
    + `DataStreamSource`类
    + `DataStream<T>`类，核心类，表示一个连续的数据流。
      提供了各种转换操作（如 `map`, `filter`, `keyBy`, `process` 等）。
    + `SingleOutputStreamOperator<T>`类，继承自 `DataStream<T>`。
      表示一个单一输出的操作符，如 `map`, `filter` 等。
    + `KeyedStream<T, KEY>`类，继承自 `DataStream<T>`，表示一个按键分组后的数据流。
      提供了键控操作（如 `process`, `reduce`, `window` 等）。
    + `WindowedStream<T, K, W extends Window>`类，开窗流
    + `JoinedStreams<T1, T2>`类，关联流
    + `AllWindowedStream<T, W extends Window>`，全局开窗流
    + `BroadcastStream<T>`，广播流
+ **`functions`**
    + `ProcessFunction<I, O>`，抽象类，继承于`AbstractRichFunction`
    + `KeyedProcessFunction<K, I, O>`，抽象类，也继承于`AbstractRichFunction`
    + `source`
        + `SourceFunction<T>`接口
    + `sink`
        + `SinkFunction<IN>`接口
    + `windowing`
        + `WindowFunction<IN, OUT, KEY, W extends Window>`接口
        + `AllWindowFunction<IN, OUT, W extends Window>`接口
        + `ProcessWindowFunction<IN, OUT, KEY, W extends Window>`抽象类，继承于`AbstractRichFunction`
    + `timestamps`
        + `BoundedOutOfOrdernessTimestampExtractor<T>`抽象类，只有这一个
    + `aggregation`
        + `AggregationFunction<T>`抽象类，继承于`ReduceFunction<T>`
+ `watermark`
    + 只有一个`Watermark`类，这个类只是一个数据类，没有提供太多方法
+ `transformations`
+ `operators`

`runtime`

`util`

------