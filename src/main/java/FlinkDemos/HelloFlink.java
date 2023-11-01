package FlinkDemos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.flink.core.fs.Path;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.kafka.common.TopicPartition;


public class HelloFlink {

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

        // 从文件中读取
        FileSource<String> fileSource = FileSource.forRecordStreamFormat(new TextLineInputFormat(),
                new Path("src/main/resources/hadoop_data/wordcount_input")).build();
        DataStreamSource<String> fileStream = env.fromSource(fileSource, WatermarkStrategy.noWatermarks(), "file");
        //fileStream.print();

        //从kafka读取数据
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
        kafkaStream.print("kafka");

        env.execute();
    }
}
