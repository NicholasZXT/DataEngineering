package FlinkDemos;

import java.time.Duration;
import java.time.ZoneId;


import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.kafka.clients.producer.ProducerConfig;

/**
 * 演示Flink的Sink算子使用
 */
public class SinkOperations {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 每个目录中，都有 并行度个数的 文件在写入
        env.setParallelism(2);
        // 必须开启checkpoint，否则一直都是 .inprogress —— 对于 file 和 kafka 都是必须的
        env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);

        // 从集合中创建源数据
        DataStreamSource<WaterSensor> sensorDS = env.fromElements(
                new WaterSensor("s1", 1.0, 1),
                new WaterSensor("s1", 11.0, 11),
                new WaterSensor("s2", 2.0, 2),
                new WaterSensor("s3", 3.0, 3)
        );

        // 输出到文件
        FileSink<String> fileSink = FileSink
                // 输出行式存储的文件，指定路径、指定编码
                .<String>forRowFormat(new Path("flink-out"), new SimpleStringEncoder<>("UTF-8"))
                // 输出文件的一些配置： 文件名的前缀、后缀
                .withOutputFileConfig(OutputFileConfig.builder()
                        .withPartPrefix("file-sink-")
                        .withPartSuffix(".log")
                        .build()
                )
                // 按照目录分桶：如下，就是每个小时一个目录
                .withBucketAssigner(new DateTimeBucketAssigner<>("yyyy-MM-dd HH", ZoneId.systemDefault()))
                // 文件滚动策略:  1分钟 或 1m
                .withRollingPolicy(
                        DefaultRollingPolicy.builder()
                                .withRolloverInterval(Duration.ofMinutes(2))       // 每 2 分钟轮换一次文件
                                .withMaxPartSize(new MemorySize(1024*1024))  // 每个文件最大 1Mb
                                .build()
                )
                .build();
        // 这里需要先转成 string
        //sensorDS.map(WaterSensor::toString).sinkTo(fileSink);

        // 写入kafka
        KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
                // 指定 kafka 的地址和端口
                .setBootstrapServers("hadoop102:9092,hadoop103:9092,hadoop104:9092")
                // 指定序列化器：指定Topic名称、具体的序列化
                .setRecordSerializer(
                        KafkaRecordSerializationSchema.<String>builder()
                                .setTopic("flink-sink")
                                .setValueSerializationSchema(new SimpleStringSchema())
                                .build()
                )
                // 写到kafka的一致性级别： 精准一次、至少一次
                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                // 如果是精准一次，必须设置 事务的前缀
                .setTransactionalIdPrefix("kafka-sink-")
                // 如果是精准一次，必须设置 事务超时时间: 大于checkpoint间隔，小于 max 15分钟
                .setProperty(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 10*60*1000+"")
                .build();

        //sensorDS.map(WaterSensor::toString).sinkTo(kafkaSink);

        env.execute();
    }
}
