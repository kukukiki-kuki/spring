package com.kafka.learning.config;

public class AppConfig {
    // 如果连接云服务器，请将 localhost 替换为云服务器的公网IP
    public static final String BOOTSTRAP_SERVERS = "117.72.189.226:9092";
    
    public static final String TOPIC_NAME = "learning-topic";
    public static final String GROUP_ID = "learning-group-1";
}