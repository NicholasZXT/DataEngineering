package KafkaDemos;

import java.util.Collections;
import java.util.Properties;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.PartitionInfo;

public class HelloConsumer implements Runnable{
    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String groupId = "test-group";
        String topic = "test-topic";
        HelloConsumer consumer = new HelloConsumer(bootstrapServers, groupId, topic);
        //consumer.printTopics();
        //consumer.run();
        consumer.seekingToStart();

        // 使用一个单独的线程，接受 Ctrl + C 信号，停止消费者 —— 尚未验证成功
        //Runtime.getRuntime().addShutdownHook(new Thread() {
        //    public void run() {
        //        consumer.waitingShutdown();
        //    }
        //});

        Thread thread = new Thread(consumer, "consumer-thread");
        thread.start();

    }

    private final KafkaConsumer<String, String> consumer;
    static Logger logger = LogManager.getLogger(HelloConsumer.class.getSimpleName());

    public HelloConsumer(String boostrapServers, String groupId, String topic){
        // 使用 Properties 封装 kafka 客户端的配置
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        configs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        //
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.consumer = new KafkaConsumer<String, String>(configs);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public void printTopics(){
        Map<String, List<PartitionInfo>> topics = this.consumer.listTopics();
        for (String t: topics.keySet()){
            logger.info("topic {} with partition info: ", t);
            List<PartitionInfo> partitions = topics.get(t);
            for (PartitionInfo partition: partitions){
                logger.info(partition.toString());
            }
            logger.info("--------------------------------------------------------------------------------------------");
        }
    }

    public void seekingToStart(){
        // kafka消费者重置offset的两种方法：
        // 1. 每次都用一个新的 group_id + 配置里 auto.offset.reset=earliest
        // 2. 使用下面的 seekToBeginning() 方法，重置被分配的partition的 offset，但是这种方式要求首先进行一次 poll，拿到当前消费者被
        //    分配的partition，在被分配的partition上 seek 到最开始
        this.consumer.poll(100);
        // 获取当前消费者被分配的 partitions
        Set<TopicPartition> partitions = this.consumer.assignment();
        logger.info("Consumer seeking to the beginning of the following partition: ");
        for (TopicPartition partition: partitions){
            logger.info(partition.toString());
        }
        this.consumer.seekToBeginning(this.consumer.assignment());
    }

    public void run() {
        try {
            //System.out.println("consumer prepares to poll data...");
            logger.info("consumer prepares to poll data...");
            while (true) {
                ConsumerRecords<String, String> records = this.consumer.poll(100);
                //System.out.println("consumer polled " + Integer.toString(records.count()) + " records...");
                logger.info("consumer polled {} records...", Integer.toString(records.count()));
                for (ConsumerRecord<String, String> record : records) {
                    //System.out.printf("topic=%s, partition=%s, offset=%d, key=%s, value=%s%n%n", record.topic(),
                    //        record.partition(), record.offset(), record.key(), record.value());
                    logger.info("{topic={}, partition={}, offset={}, key={}, value={}}", record.topic(),
                            record.partition(), record.offset(), record.key(), record.value());
                }
            }
        } catch (WakeupException exception){
            //System.out.println("Kafka Consumer receive Wakeup...");
            logger.warn("Kafka Consumer receive Wakeup...");
            exception.printStackTrace();
        } finally {
            this.consumer.close();
            //System.out.println("Kafka Consumer was closed.");
            logger.warn("Kafka Consumer was closed.");
        }
    }

    public void shutdown() {
        // 唤醒消费线程
        this.consumer.wakeup();
        //System.out.println("Shutdown consumer...");
        logger.warn("Shutdown consumer...");
    }

    public void waitingShutdown(){
        while (true){
            try {
                Thread.sleep(1000);
                logger.info("waiting shutdown thread is looping...");
            } catch (InterruptedException e) {
                //System.out.println("Receiving Shutdown Interruption, prepares to stop consumer...");
                logger.info("Receiving Shutdown Interruption, prepares to stop consumer...");
                e.printStackTrace();
                this.shutdown();
                break;
            }
        }
    }
}
