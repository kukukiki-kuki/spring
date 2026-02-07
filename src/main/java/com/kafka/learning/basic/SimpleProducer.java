package com.kafka.learning.basic;

import com.kafka.learning.config.AppConfig;
import com.kafka.learning.config.AppConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * 基础生产者示例
 * 
 * 包含：
 * 1. 生产者配置详解
 * 2. 三种发送模式：发后即忘、同步发送、异步发送
 */
public class SimpleProducer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
    private static final String TOPIC_NAME = "learning-topic";

    public static void main(String[] args) {
        Properties props = new Properties();
        // 1. 必需配置
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.BOOTSTRAP_SERVERS);
        // Key 和 Value 的序列化器：Kafka 只处理字节数组，所以需要将对象序列化
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 2. 优化配置（重要）
        // acks: 确认机制
        // "0": 生产者不等待服务器确认，最快但最不可靠（可能丢数据）
        // "1": Leader 写入成功即确认，折中方案
        // "all" / "-1": ISR（In-Sync Replicas）中所有副本都写入成功才确认，最慢但最可靠
        props.put(ProducerConfig.ACKS_CONFIG, "1");

        // retries: 重试次数。如果发送失败（例如网络抖动），客户端会自动重试
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        // batch.size: 批处理大小（字节）。Producer 会尝试将发往同一个分区的消息打包发送，提高吞吐量
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);

        // linger.ms: 等待时间。即使 batch 没满，达到这个时间也会发送。
        // 设置为 0 表示立即发送（低延迟）；设置 > 0 可以增加吞吐量（减少请求次数）
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        Producer<String, String> producer = new KafkaProducer<>(props);

        try {
            // 模式 1: 发后即忘 (Fire-and-forget)
            // 缺点：不知道是否发送成功，可能丢失数据
            // producer.send(new ProducerRecord<>(TOPIC_NAME, "key-1", "value-fire-and-forget"));

            // 模式 2: 同步发送 (Synchronous send)
            // 缺点：阻塞等待，吞吐量低
            try {
                logger.info("开始同步发送...");
                RecordMetadata metadata = producer.send(new ProducerRecord<>(TOPIC_NAME, "key-sync", "value-sync")).get();
                logger.info("同步发送成功: topic={}, partition={}, offset={}", 
                    metadata.topic(), metadata.partition(), metadata.offset());
            } catch (ExecutionException | InterruptedException e) {
                logger.error("同步发送失败", e);
            }

            // 模式 3: 异步发送 (Asynchronous send) - 推荐
            // 优点：非阻塞，吞吐量高，通过回调处理结果
            logger.info("开始异步发送...");
            for (int i = 0; i < 5; i++) {
                String key = "key-async-" + i;
                String value = "value-async-" + i;
                
                producer.send(new ProducerRecord<>(TOPIC_NAME, key, value), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception == null) {
                            logger.info("异步发送成功: key={}, partition={}, offset={}", key, metadata.partition(), metadata.offset());
                        } else {
                            logger.error("异步发送失败: key=" + key, exception);
                        }
                    }
                });
            }

        } finally {
            // 关闭生产者，确保缓存中的消息都被发送
            producer.close();
        }
    }
}
