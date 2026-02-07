package com.kafka.learning.advanced;

import com.kafka.learning.config.AppConfig;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * 手动提交 Offset 消费者示例
 * 
 * 知识点：
 * 1. 关闭自动提交 (enable.auto.commit = false)
 * 2. 同步提交 (commitSync) vs 异步提交 (commitAsync)
 * 3. 消息处理与提交顺序的重要性 (At-least-once vs At-most-once)
 */
public class ManualCommitConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ManualCommitConsumer.class);
    private static final String TOPIC_NAME = "learning-topic";
    private static final String GROUP_ID = "manual-commit-group";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 关键点：关闭自动提交
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                if (records.isEmpty()) continue;

                for (ConsumerRecord<String, String> record : records) {
                    logger.info("处理消息: key={}, value={}, offset={}", record.key(), record.value(), record.offset());
                    // 模拟业务处理耗时
                    // process(record); 
                }

                // 提交方式 1: 同步提交
                // 优点：可靠，会重试直到成功或抛出异常
                // 缺点：阻塞，降低吞吐量
                try {
                    consumer.commitSync(); 
                    logger.info("同步提交成功");
                } catch (CommitFailedException e) {
                    logger.error("提交失败", e);
                }

                // 提交方式 2: 异步提交 (通常在循环中使用异步，在关闭前使用同步)
                // 优点：非阻塞，高吞吐
                // 缺点：由于没有重试机制（为了避免 Offset 覆盖问题），可能失败
                /*
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        logger.error("异步提交失败: {}", offsets, exception);
                    }
                });
                */
            }
        } catch (Exception e) {
            logger.error("消费者异常", e);
        } finally {
            try {
                // 确保在退出前最后一次提交成功
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
