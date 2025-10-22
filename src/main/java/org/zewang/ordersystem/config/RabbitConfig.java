package org.zewang.ordersystem.config;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 19:55
 */

@Slf4j
@Configuration
@EnableRabbit
public class RabbitConfig {

    @PostConstruct
    public void checkRabbitConfig() {
        System.out.println("RabbitConfig initialized");
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("消息发送成功: correlationData={}", correlationData);
            } else {
                log.error("消息发送失败: correlationData={}, cause={}", correlationData, cause);
            }
        });

        template.setReturnsCallback(returned -> {
            log.warn("消息无法路由到队列: exchange={}, routingKey={}, replyCode={}, replyText={}, message={}",
                returned.getExchange(), returned.getRoutingKey(), returned.getReplyCode(),
                returned.getReplyText(), returned.getMessage());
        });

        template.setMandatory(true);

        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
        SimpleRabbitListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }


    // === 订单 ===

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(RabbitConstants.ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderProcessingQueue() {
        return QueueBuilder.durable(RabbitConstants.ORDER_PROCESSING_QUEUE).build();
    }

    @Bean
    public Binding orderProcessingBinding() {
        return BindingBuilder.bind(orderProcessingQueue()).to(orderExchange()).with(RabbitConstants.ORDER_PROCESSING_ROUTING_KEY);
    }

    @Bean
    public Queue orderDLQ() {
        return QueueBuilder.durable(RabbitConstants.ORDER_DLQ_QUEUE).build();
    }

    @Bean
    public Binding orderDLQBinding() {
        return BindingBuilder.bind(orderDLQ()).to(orderExchange()).with(RabbitConstants.ORDER_DLQ_ROUTING_KEY);
    }


    // === 库存 ===

    // 主交换机
    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange(RabbitConstants.INVENTORY_EXCHANGE);
    }

    // 库存扣减队列
    @Bean
    public Queue inventoryDeductQueue() {
        return QueueBuilder.durable(RabbitConstants.INVENTORY_DEDUCT_QUEUE)
            .withArgument("x-dead-letter-exchange", RabbitConstants.INVENTORY_DLQ_EXCHANGE)
            .build();
    }

    // 库存扣减队列绑定到交换机
    @Bean
    public Binding inventoryDeductBinding() {
        return BindingBuilder.bind(inventoryDeductQueue())
            .to(inventoryExchange())
            .with(RabbitConstants.INVENTORY_DEDUCT_ROUTING_KEY);
    }

    // 死信队列
    @Bean
    public Queue inventoryDeadLetterQueue() {
        return QueueBuilder.durable(RabbitConstants.INVENTORY_DLQ_QUEUE).build();
    }

    // 死信交换机
    @Bean
    public DirectExchange inventoryDeadLetterExchange() {
        return new DirectExchange(RabbitConstants.INVENTORY_DLQ_EXCHANGE);
    }

    // 绑定死信队列
    @Bean
    public Binding inventoryDeadLetterBinding() {
        return BindingBuilder.bind(inventoryDeadLetterQueue())
            .to(inventoryDeadLetterExchange())
            .with(RabbitConstants.INVENTORY_DLQ_ROUTING_KEY);
    }

    // 库存初始化队列
    @Bean
    public Queue inventoryInitQueue() {
        return QueueBuilder.durable(RabbitConstants.INVENTORY_INIT_QUEUE)
            .withArgument("x-dead-letter-exchange", RabbitConstants.INVENTORY_DLQ_EXCHANGE)
            .build();
    }

    // 库存初始化队列绑定
    @Bean
    public Binding inventoryInitBinding() {
        return BindingBuilder.bind(inventoryInitQueue())
            .to(inventoryExchange())
            .with(RabbitConstants.INVENTORY_INIT_QUEUE);
    }

    // 库存调整队列
    @Bean
    public Queue inventoryAdjustQueue() {
        return QueueBuilder.durable(RabbitConstants.INVENTORY_ADJUST_QUEUE)
            .withArgument("x-dead-letter-exchange", RabbitConstants.INVENTORY_DLQ_EXCHANGE)
            .build();
    }

    // 库存调整队列绑定
    @Bean
    public Binding inventoryAdjustBinding() {
        return BindingBuilder.bind(inventoryAdjustQueue())
            .to(inventoryExchange())
            .with(RabbitConstants.INVENTORY_ADJUST_ROUTING_KEY);
    }
}

