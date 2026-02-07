package com.kafka.learning.advanced;

import com.kafka.learning.config.AppConfig;
import com.kafka.learning.config.AppConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.UUID;

/**
 * 事务生产者示例
 * 
 * 知识点：
 * 1. 幂等性 (Idempotence): 保证单分区内消息不丢失、不重复、有序。
 * 2. 事务 (Transaction): 保证跨分区、跨 Topic 的多条消息写入的原子性（要么全成功，要么全失败）。
 *    常用于 "Consume-Process-Produce" 场景（如 Kafka Streams）。
 */
public class TransactionalProducer {
    private static final Logger logger = LoggerFactory.getLogger(TransactionalProducer.class);

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 必须设置 transactional.id，这会自动开启幂等性 (enable.idempotence = true)
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "tx-id-" + UUID.randomUUID().toString());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // 初始化事务
        producer.initTransactions();

        try {
            // 开启事务
            producer.beginTransaction();

            // 发送多条消息
            producer.send(new ProducerRecord<>("learning-topic", "tx-key-1", "tx-value-1"));
            producer.send(new ProducerRecord<>("learning-topic", "tx-key-2", "tx-value-2"));

            // 模拟异常，测试回滚
            // if (true) throw new RuntimeException("模拟业务异常");

            // 提交事务
            producer.commitTransaction();
            logger.info("事务提交成功");

        } catch (Exception e) {
            logger.error("事务失败，进行回滚", e);
            // 回滚事务
            producer.abortTransaction();
        } finally {
            producer.close();
        }
    }
}
