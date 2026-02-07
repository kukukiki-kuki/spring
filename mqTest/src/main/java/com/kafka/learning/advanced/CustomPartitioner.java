package com.kafka.learning.advanced;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * 自定义分区器
 * 
 * 作用：决定消息发送到 Topic 的哪个分区。
 * 默认分区策略：
 * 1. 如果指定了 partition，则使用指定的。
 * 2. 如果没指定 partition 但有 key，则按 key 的 hash 值取模。
 * 3. 如果既没 partition 也没 key，则轮询（Sticky Partitioning 优化）。
 */
public class CustomPartitioner implements Partitioner {

    @Override
    public void configure(Map<String, ?> configs) {
        // 获取配置信息
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();

        // 示例逻辑：
        // 如果 key 是 "vip"，专门发到最后一个分区
        // 其他 key 按照 hash 分配到其他分区
        
        if (keyBytes == null || !(key instanceof String)) {
            // 没有 Key，随机/轮询（这里简单处理为0）
            return 0;
        }

        String keyStr = (String) key;
        if ("vip".equals(keyStr)) {
            return numPartitions - 1; // 最后一个分区
        }

        // 其他 key 分配到 0 到 numPartitions-2
        return (Math.abs(Utils.murmur2(keyBytes)) % (numPartitions - 1));
    }

    @Override
    public void close() {
        // 清理资源
    }
}
