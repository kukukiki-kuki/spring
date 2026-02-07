package com.kafka.learning.advanced;

import com.kafka.learning.config.AppConfig;
import com.kafka.learning.config.AppConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * 使用拦截器的生产者
 */
public class InterceptorProducer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, AppConfig.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // 配置拦截器链（可以有多个，逗号分隔）
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, TraceProducerInterceptor.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            for (int i = 0; i < 10; i++) {
                producer.send(new ProducerRecord<>("learning-topic", "key-" + i, "value-" + i));
            }
        }
        // 拦截器的 close 方法会在 producer.close() 时调用，打印统计信息
    }
}
