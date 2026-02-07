# Kafka Learning Lab

这是一个用于学习 Apache Kafka 的 Java 项目。本项目包含了从基础的生产者/消费者到高级特性（如事务、拦截器、自定义分区器）的完整示例代码，并附带了详细的中文注释。

## 🚀 快速开始

### 1. 环境准备

**前置要求**: 必须安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)。

安装完成后，在项目根目录下运行以下命令启动环境：

```bash
# 推荐：使用项目自带的辅助脚本（会自动检测并执行正确的命令）
./start-kafka.bat

# 或者手动执行（根据 Docker 版本不同，使用以下命令之一）：
docker compose up -d
# 或
docker-compose up -d
```

这将启动：
- **Zookeeper**: 监听 2181 端口
- **Kafka Broker**: 监听 9092 (宿主机) 和 29092 (Docker网络内)

### 1.1 云服务器部署指南 (推荐)

如果你本地电脑运行 Docker Desktop 卡顿，强烈建议使用云服务器。

#### 第一步：在云服务器安装 Docker
**CentOS:**
```bash
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io
sudo systemctl start docker
sudo systemctl enable docker
```

**Ubuntu:**
```bash
sudo apt-get update
sudo apt-get install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker
```

#### 第二步：部署 Kafka
1. 将项目根目录下的 `docker-compose-cloud.yml` 文件上传到云服务器。
2. 编辑该文件，将 `YOUR_PUBLIC_IP` 替换为云服务器的**真实公网 IP**。
3. 运行容器：
   ```bash
   # 如果安装了 Docker Compose V2 (新版)
   docker compose -f docker-compose-cloud.yml up -d
   
   # 或者旧版命令
   docker-compose -f docker-compose-cloud.yml up -d
   ```
4. **重要**: 在云服务商控制台（阿里云/腾讯云/AWS等）的安全组规则中，开放 `9092` 和 `2181` 端口。

#### 第三步：修改本地配置
1. 在本地项目中创建 `src/main/java/com/kafka/learning/config/AppConfig.java`。
2. 将 `BOOTSTRAP_SERVERS` 修改为 `"你的云服务器公网IP:9092"`。

### 2. 运行代码

建议按照以下顺序阅读和运行代码：

1. **创建 Topic**
   - 运行 `com.kafka.learning.admin.TopicManager`
   - 作用：创建一个名为 `learning-topic` 的主题，包含 3 个分区。

2. **基础生产者**
   - 运行 `com.kafka.learning.basic.SimpleProducer`
   - 观察：控制台输出同步和异步发送的结果。

3. **基础消费者**
   - 运行 `com.kafka.learning.basic.SimpleConsumer`
   - 观察：接收并打印消息。

4. **进阶场景**
   - **手动提交 Offset**: `com.kafka.learning.advanced.ManualCommitConsumer`
   - **自定义分区器**: `com.kafka.learning.advanced.PartitionerProducer`
   - **事务与幂等性**: `com.kafka.learning.advanced.TransactionalProducer`
   - **拦截器**: `com.kafka.learning.advanced.InterceptorProducer`

---

## 📚 核心概念解析

### 1. 基础架构
- **Producer (生产者)**: 发送消息的客户端。
- **Consumer (消费者)**: 接收消息的客户端。
- **Broker**: Kafka 服务节点。
- **Topic (主题)**: 消息的逻辑分类。
- **Partition (分区)**: Topic 的物理分片，用于实现扩展性。**分区内的消息是有序的，但 Topic 整体不保证有序。**
- **Replica (副本)**: 分区的备份，用于高可用。Leader 副本负责读写，Follower 副本只负责同步。

### 2. 关键配置说明

#### 生产者 (Producer)
- `acks`: 决定可靠性级别。
  - `0`: 不等待确认（最快，可能丢数据）。
  - `1`: Leader 确认即可（默认，平衡）。
  - `all` / `-1`: 所有 ISR 确认（最慢，最安全）。
- `retries`: 发送失败后的重试次数。
- `batch.size` & `linger.ms`: 吞吐量优化神器。允许生产者积攒一小批数据再发送，而不是一条一条发。

#### 消费者 (Consumer)
- `group.id`: 消费者组。组内成员共同分担消费任务（负载均衡）。不同组之间是发布订阅模式（广播）。
- `enable.auto.commit`: 自动提交 Offset。虽然方便，但在追求"仅一次处理"或"至少一次处理"时，通常建议设为 `false` 并手动提交。
- `auto.offset.reset`: 当没有历史 Offset 时从哪里开始读。`earliest` (从头), `latest` (只读新的)。

---

## 🛠 常见异常与排查思路

### 1. 连接问题 (Connection Refused / Timeout)
- **现象**: 客户端报错无法连接到 Broker。
- **排查**:
  - 检查 Kafka 是否启动。
  - **重点检查 `advertised.listeners` 配置**。客户端拿到的 Broker 地址必须是客户端所在网络能访问的地址。
  - 检查防火墙端口 (9092)。

### 2. 消息积压 (Lag)
- **现象**: 生产速度远大于消费速度，导致 Lag 值持续升高。
- **排查**:
  - 增加消费者数量（但不能超过分区数）。
  - 优化消费逻辑（比如将耗时操作改为异步处理）。
  - 增加 `max.poll.records` 限制每次拉取数量，防止单次处理太久导致 Session 超时。

### 3. 重平衡风暴 (Rebalance Storm)
- **现象**: 消费者频繁加入/离开组，导致无法正常消费。
- **原因**:
  - 消费者处理太慢，超过了 `max.poll.interval.ms`，被 Coordinator 判定为死亡。
  - 心跳超时 `session.timeout.ms`。
- **解决**:
  - 增加 `max.poll.interval.ms`。
  - 减小 `max.poll.records`。
  - 优化业务逻辑。

### 4. 消息丢失
- **排查**:
  - **生产端**: 检查 `acks` 是否为 `0` 或 `1`？建议关键业务设为 `all`。
  - **消费端**: 是否开启了自动提交？如果自动提交了但业务处理失败，消息就丢了。建议手动提交 (`commitSync` / `commitAsync`)。

### 5. 消息重复
- **排查**:
  - **生产端**: 网络抖动导致 ACK 丢失，生产者重试。解决：开启幂等性 (`enable.idempotence=true`)。
  - **消费端**: 消费了但没来得及提交 Offset 就挂了。解决：业务逻辑实现幂等性（如利用数据库唯一键）。

---

## 📂 项目结构

```
src/main/java/com/kafka/learning
├── admin
│   └── TopicManager.java       // Topic 管理（创建/删除）
├── basic
│   ├── SimpleProducer.java     // 基础生产者（同步/异步）
│   └── SimpleConsumer.java     // 基础消费者（自动提交）
└── advanced
    ├── ManualCommitConsumer.java // 手动提交 Offset
    ├── CustomPartitioner.java    // 自定义分区策略
    ├── PartitionerProducer.java  // 使用自定义分区的生产者
    ├── TransactionalProducer.java// 事务生产者
    ├── TraceProducerInterceptor.java // 拦截器实现
    └── InterceptorProducer.java  // 使用拦截器的生产者
```
