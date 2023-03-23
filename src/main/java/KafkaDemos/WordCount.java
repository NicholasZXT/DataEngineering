package KafkaDemos;

import java.util.Properties;
import java.util.Arrays;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;


/**
 *   KafkaStream 对象是一个客户端，用于处理流式的数据，它具有 start() 和 close() 方法用于控制客户端
 *   KStream 对象是对流式数据中 每一个record 的抽象表示，它是 K-V 的键值对形式，该对象有一系列的转化方法，比如 MapValue, Group 等操作，
 *   转换成另一个 KStream 对象
 */

public class WordCount {

    public static void main(String[] args) throws InterruptedException {
        // 第 1 步：添加配置
        // 创建一个Properties对象，用于存放配置
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Word-Count");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // 第 2 步： 首先创建一个 builder
        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> source = builder.stream("wordcount-input");
        // source.print(Printed.<String, String>toSysOut().withLabel("word-count input"));

        // 第 3 步：逐个添加处理节点，下面展示了 3 种
        // KStream 对象的每一步操作，都会返回一个新的 KStream 对象
        // 3.1，简单的写入另一个索引
        //source.to("streams-word-count-output");
        // 3.2，对每一行的单词做切分，然后直接写入topic
        //source.flatMapValues(value -> Arrays.asList(value.split("\\W+"))).to("streams-word-count-output");
        // 3.3，真正实现统计单词个数
        //source.flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
        KStream<String, Long> res = source.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, value) -> value)
                .count(Materialized.as("WordCount"))
                .toStream();

        // res.print(Printed.<String,Long>toSysOut().withLabel("word-count res"));
        res.to("wordcount-output", Produced.with(Serdes.String(), Serdes.Long()));

        // 第 4 步：构建拓扑
        final Topology topology = builder.build();
        // 打印拓扑信息
        System.out.println(topology.describe());

        // 第 5 步：由拓扑和配置属性构建 KafkaStream 对象
        final KafkaStreams kstream = new KafkaStreams(topology, props);

        // 第 6 步：启动 以及 关闭 KStream
        kstream.start();
        //Thread.sleep(5000L);
        //kstream.close();

    }

}
