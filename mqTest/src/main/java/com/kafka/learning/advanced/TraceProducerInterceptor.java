package com.kafka.learning.advanced;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 生产者拦截器
 * 
 * 作用：
 * 1. onSend: 在消息被序列化和计算分区之前调用。可以修改消息（如添加 Header、修改 Value）。
 * 2. onAcknowledgement: 在消息被服务器确认或发送失败时调用（早于 Callback）。
 * 
 * 常见用途：
 * - 埋点监控
 * - 消息审计
 * - 统一修改消息内容
 */
public class TraceProducerInterceptor implements ProducerInterceptor<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(TraceProducerInterceptor.class);
    private int successCount = 0;
    private int errorCount = 0;

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        // 示例：给每条消息 value 加上前缀
        return new ProducerRecord<>(
                record.topic(),
                record.partition(),
                record.timestamp(),
                record.key(),
                "Intercepted-" + record.value(), // 修改 value
                record.headers()
        );
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            successCount++;
        } else {
            errorCount++;
        }
    }

    @Override
    public void close() {
        logger.info("发送统计: 成功={}, 失败={}", successCount, errorCount);
    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
