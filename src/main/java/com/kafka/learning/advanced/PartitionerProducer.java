package com.kafka.learning.advanced;

import com.kafka.learning.config.AppConfig;
import com.kafka.learning.config.AppConfig;
import com.kafka.learning.config.AppConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * 使用自定义分区器的生产者
 */
public class PartitionerProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // 指定自定义分区器
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            // 这条消息会去最后一个分区
            producer.send(new ProducerRecord<>("learning-topic", "vip", "vip-value"));
            
            // 这条消息会去其他分区
            producer.send(new ProducerRecord<>("learning-topic", "normal", "normal-value"));
        }
    }
}
