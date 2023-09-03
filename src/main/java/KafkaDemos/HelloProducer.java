package KafkaDemos;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;


public class HelloProducer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 使用 Properties 封装 kafka 客户端的配置
        Properties configs = new Properties();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.ACKS_CONFIG, "1");
        configs.put(ProducerConfig.RETRIES_CONFIG, 3);
        configs.put(ProducerConfig.BATCH_SIZE_CONFIG, 16);

        String topic = "test-topic";
        String key_prefix = "key-";

        Producer<String, String> producer = new KafkaProducer<String, String>(configs);
        for (int i = 0; i <=5; i++){
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key_prefix + Integer.toString(i), Integer.toString(i));
            Future<RecordMetadata> future = producer.send(record);
            // 使用 future 对象的 get() 方法就是同步发送消息，因为 get 方法会阻塞
            RecordMetadata recordMetadata = future.get();
            System.out.println(recordMetadata.toString());
        }

    }
}
