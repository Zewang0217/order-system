# 异步订单后端系统

这是一个基于Spring Boot和RabbitMQ构建的异步订单处理系统，实现了订单管理、支付处理、库存管理和物流跟踪等核心功能。

## 系统架构

本系统采用微服务设计理念，通过消息队列实现服务间的异步通信：

- **订单服务**：处理订单创建、查询和状态管理
- **支付服务**：处理支付请求和回调
- **库存服务**：管理商品库存和扣减逻辑
- **物流服务**：处理发货和物流跟踪信息

各服务通过RabbitMQ进行异步通信，确保系统的高可用性和可扩展性。

## 核心功能

### 1. 订单管理
- 订单创建与查询
- 订单状态跟踪
- 订单取消与退款

### 2. 支付处理
- 多种支付方式支持（支付宝、微信、信用卡、银行转账）
- 支付回调处理
- 支付状态同步

### 3. 库存管理
- 商品库存查询
- 订单触发的库存扣减
- 库存调整与初始化

### 4. 物流跟踪
- 发货单生成
- 物流信息跟踪
- 物流状态更新

## 技术栈

- **后端框架**：Spring Boot 3.x
- **安全框架**：Spring Security + JWT
- **数据库**：MySQL 8.x
- **ORM框架**：MyBatis-Plus
- **消息队列**：RabbitMQ
- **API文档**：SpringDoc OpenAPI
- **构建工具**：Maven

## 系统设计亮点

### 异步消息处理
系统通过RabbitMQ实现服务间异步通信，确保高并发场景下的系统稳定性：

```java
// 消息消费者示例
@RabbitListener(queues = "${rabbitmq.queue.payment.success:payment.success.queue}", ackMode = "MANUAL")
@Transactional
public void handlePaymentSuccessMessage(Message message, Channel channel) {
    // 处理支付成功消息
}
```


### 可靠消息传递
系统实现了基于Outbox模式的消息可靠性保证：

- 消息持久化存储
- 发送状态跟踪
- 失败重试机制
- 死信队列处理

### 完整的日志跟踪
所有关键业务操作都有详细日志记录，便于问题排查和系统监控。

## 数据库设计

主要数据表包括：

- `orders`：订单主表
- `order_items`：订单明细表
- `payments`：支付记录表
- `inventories`：库存表
- `inventory_histories`：库存变更历史表
- `fulfillments`：发货单表
- `shipment_tracks`：物流轨迹表
- `outbox_messages`：消息发件箱表
- `dead_letter_messages`：死信消息表

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/order_system
    username: root
    password: root
```


### RabbitMQ配置
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```


## 部署说明

1. 确保安装并启动MySQL和RabbitMQ服务
2. 执行[db.sql](file://D:\Zewang\PROJECTS\order-system\target\classes\db.sql)脚本创建数据库表
3. 配置[application.yml](file://D:\Zewang\PROJECTS\order-system\target\classes\application.yml)中的数据库和消息队列连接信息
4. 使用Maven构建项目：`mvn clean package`
5. 运行应用：`java -jar target/order-system-*.jar`

## API安全

系统采用JWT Token进行API安全验证：
- 所有API请求需要在Header中携带有效的Authorization Token
- Token通过用户登录接口获取
- 不同用户角色具有不同的API访问权限

## 监控与日志

- 系统通过Logback记录详细运行日志
- 关键业务操作都有INFO级别日志记录
- 异常情况会记录ERROR级别日志便于问题排查
- 可通过日志分析系统运行状态和性能瓶颈

## 扩展性考虑

- 服务间通过消息队列解耦，便于独立扩展
- 数据库表设计考虑了水平分表扩展需求
- 支持多实例部署实现负载均衡
- 消息处理具有幂等性，支持重复消费

## 注意事项

1. 系统启动前确保MySQL和RabbitMQ服务正常运行
2. 首次运行需要执行数据库初始化脚本
3. 生产环境建议修改默认的用户名密码
4. 根据实际业务需求调整消息队列配置参数
