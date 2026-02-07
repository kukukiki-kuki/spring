package com.spring.learning.week6.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 考题 3: Kafka 事务性消费
 * 
 * 目标：
 * 实现一个消费者，它读取 "input-topic" 的消息，处理后转发到 "output-topic"。
 * 要求整个过程是事务性的（Read-Process-Write）。
 * 
 * 提示：
 * - 确保配置了 KafkaTransactionManager (Spring Boot 自动配置通常会处理)
 * - 使用 @Transactional 注解保证事务
 * - 使用 KafkaTemplate 发送消息
 */
@Component
public class TransactionalConsumerTask {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 监听 input-topic，处理消息，并发到 output-topic
     * 消息内容简单转换：比如转大写，并在前面加上 "Processed: "
     */
    @KafkaListener(topics = "input-topic", groupId = "tx-group")
    @Transactional
    public void listen(String input) {
        // TODO: 请实现业务逻辑
        // 1. 处理消息 (e.g. input.toUpperCase())
        // 2. 发送到 "output-topic"
        
        throw new UnsupportedOperationException("请实现 listen 方法");
    }
}
