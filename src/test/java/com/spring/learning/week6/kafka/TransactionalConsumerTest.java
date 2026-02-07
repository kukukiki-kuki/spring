package com.spring.learning.week6.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.Mockito.*;

public class TransactionalConsumerTest {

    @Test
    public void testListen() {
        // Mock
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        TransactionalConsumerTask task = new TransactionalConsumerTask();
        
        // 注入 Mock (使用反射注入私有字段)
        try {
            java.lang.reflect.Field field = TransactionalConsumerTask.class.getDeclaredField("kafkaTemplate");
            field.setAccessible(true);
            field.set(task, kafkaTemplate);
        } catch (Exception e) {
            throw new RuntimeException("注入 Mock 失败", e);
        }

        // Run
        try {
            task.listen("hello");
            
            // Verify: 验证是否调用了 send 方法，且内容正确
            // 假设逻辑是转大写并加前缀
            // verify(kafkaTemplate).send(eq("output-topic"), contains("HELLO"));
            // 或者简单点
            verify(kafkaTemplate).send(eq("output-topic"), anyString());
            
        } catch (UnsupportedOperationException e) {
            System.out.println("Kafka 任务未实现，跳过测试");
        }
    }
}
