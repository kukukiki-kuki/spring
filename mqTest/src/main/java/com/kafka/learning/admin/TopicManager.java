package com.kafka.learning.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kafka.learning.config.AppConfig;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Kafka Topic管理工具
 * 
 * 知识点：
 * 1. AdminClient: Kafka提供的用于管理集群资源的客户端（创建/删除Topic，管理ACL，查看集群信息等）
 * 2. 为什么需要AdminClient: 虽然可以通过命令行kafka-topics.sh管理，但在程序中自动创建Topic可以简化部署
 */
public class TopicManager {
    private static final Logger logger = LoggerFactory.getLogger(TopicManager.class);
    private static final String BOOTSTRAP_SERVERS = AppConfig.BOOTSTRAP_SERVERS;

    public static void main(String[] args) {
        createTopic("learning-topic", 3, (short) 1);
    }

    public static void createTopic(String topicName, int partitions, short replicationFactor) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

        // try-with-resources 自动关闭 AdminClient
        try (AdminClient adminClient = AdminClient.create(props)) {
            // 检查Topic是否存在
            boolean exists = adminClient.listTopics().names().get().contains(topicName);
            if (exists) {
                logger.info("Topic {} 已经存在", topicName);
                return;
            }

            // 构建 NewTopic 对象
            // 参数说明：
            // 1. name: Topic名称
            // 2. numPartitions: 分区数（决定并发度）
            // 3. replicationFactor: 副本因子（决定容灾能力，单机开发环境通常设为1）
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            // 创建Topic是异步操作，返回KafkaFuture
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            logger.info("Topic {} 创建成功", topicName);

        } catch (InterruptedException | ExecutionException e) {
            logger.error("创建Topic失败", e);
        }
    }
}
