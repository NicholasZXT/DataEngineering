package FlinkDemos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.connector.datagen.source.DataGeneratorSource;
import org.apache.flink.connector.datagen.source.GeneratorFunction;
import org.apache.flink.api.connector.source.util.ratelimit.RateLimiterStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.common.TopicPartition;

/**
 * 演示Flink的源算子使用
 */
public class C1_SourceOperations {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 从集合中读取数据
        List<Integer> data = Arrays.asList(1, 22, 3);
        DataStreamSource<Integer> collectionSource = env.fromCollection(data);
        //collectionSource.print();

        DataStreamSource<WaterSensor> sensorSource = env.fromElements(
                new WaterSensor("sensor_1", 1.0, 1),
                new WaterSensor("sensor_2", 2.0, 2)
        );
        //sensorSource.print();

        // 从文件中读取，需要 flink-connector-files 依赖
        FileSource<String> fileSource = FileSource.forRecordStreamFormat(new TextLineInputFormat(),
                new Path("src/main/resources/hadoop_data/wordcount_input")).build();
        DataStreamSource<String> fileStream = env.fromSource(fileSource, WatermarkStrategy.noWatermarks(), "fileStream");
        //fileStream.print();

        // 从 socket读取
        // linux 使用 nc -lk 7890 命令；Windows下，可以使用mobaXterm
        DataStreamSource<String> socketStream = env.socketTextStream("localhost", 7890);
        //socketStream.print("socketStream");

        // 数据生成器，DataGeneratorSource 需要4个参数：
        // 1. GeneratorFunction接口，需要重写map方法， 输入类型固定是Long
        // 2. long类型，自动生成的数字序列（从0自增）的最大值(小于)，达到这个值就停止了
        // 3. 限速策略，比如每秒生成几条数据
        // 4. 返回的类型，使用 Types 指定
        DataGeneratorSource<String> dgSource = new DataGeneratorSource<>(
                new GeneratorFunction<Long, String>() {
                    @Override
                    public String map(Long value) throws Exception {
                        return "Number: " + value;
                    }
                },
                16,
                RateLimiterStrategy.perSecond(1),
                Types.STRING
        );
        DataStreamSource<String> dgStream = env.fromSource(dgSource, WatermarkStrategy.noWatermarks(), "data-generator");
        dgStream.print();


        // 从kafka读取数据，需要 flink-connector-kafka 依赖
        TopicPartition partition = new TopicPartition("first", 0);
        Set<TopicPartition> partitions = new HashSet<>();
        partitions.add(partition);
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers("hadoop101:9092,hadoop102:9092,hadoop103:9092")
                .setGroupId("f1")
                .setTopics("first")
                //.setPartitions(partitions)
                //.setProperty("partition.discovery.interval.ms", "10000")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStreamSource<String> kafkaStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kafka-source");
        // Flink 好像没有提供类似于 limit 这样的API
        //kafkaStream.print("kafka");

        env.execute();
    }
}
