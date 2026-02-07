package com.kafka.learning.basic;

import com.kafka.learning.config.AppConfig;
import com.kafka.learning.config.AppConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * 基础消费者示例
 * 
 * 包含：
 * 1. 消费者组概念
 * 2. 自动提交 Offset
 * 3. 优雅关闭
 */
public class SimpleConsumer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);
    private static final String TOPIC_NAME = "learning-topic";
    private static final String GROUP_ID = "learning-group-1";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.BOOTSTRAP_SERVERS);
        
        // 反序列化器：需要与 Producer 的序列化器对应
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // group.id: 消费者组 ID。
        // 同一个组内的消费者共同消费一个 Topic 的所有分区（负载均衡）。
        // 不同组的消费者分别消费一份完整的数据（发布/订阅）。
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);

        // enable.auto.commit: 是否自动提交偏移量 (Offset)。
        // true: 消费者会在后台定期提交 Offset，简单但可能导致重复消费或丢失数据（在处理完成前挂掉）。
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");

        // auto.offset.reset: 当没有初始 Offset 或 Offset 丢失时如何处理。
        // "earliest": 从最早的记录开始消费。
        // "latest": 只消费启动后新发送的消息（默认）。
        // "none": 抛出异常。
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // 订阅 Topic
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));

        // 注册 JVM 关闭钩子，实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("检测到关闭信号，正在关闭消费者...");
            consumer.wakeup(); // 中断 poll()，抛出 WakeupException
        }));

        try {
            while (true) {
                // poll: 拉取消息。参数是等待时间。
                // 如果没有消息，会阻塞等待 100ms 然后返回空。
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("收到消息: key={}, value={}, partition={}, offset={}", 
                        record.key(), record.value(), record.partition(), record.offset());
                }
            }
        } catch (org.apache.kafka.common.errors.WakeupException e) {
            // 忽略此异常，这是关闭的正常流程
        } catch (Exception e) {
            logger.error("消费者异常", e);
        } finally {
            consumer.close(); // 提交 Offset 并关闭连接
            logger.info("消费者已关闭");
        }
    }
}
