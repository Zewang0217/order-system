package org.zewang.ordersystem.config;


import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 19:55
 */

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
        return new RabbitTemplate(connectionFactory);
    }

    // 主交换机
    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange("inventory.exchange");
    }

    // 库存扣减队列
    @Bean
    public Queue inventoryDeductQueue() {
        return QueueBuilder.durable("inventory.deduct.queue")
            .withArgument("x-dead-letter-exchange", "inventory.dlq.exchange")
            .build();
    }

    // 库存扣减队列绑定到交换机
    @Bean
    public Binding inventoryDeductBinding() {
        return BindingBuilder.bind(inventoryDeductQueue())
            .to(inventoryExchange())
            .with("inventory.deduct");
    }

    // 死信队列
    @Bean
    public Queue inventoryDeadLetterQueue() {
        return QueueBuilder.durable("inventory.dlq.queue").build();
    }

    // 死信交换机
    @Bean
    public DirectExchange inventoryDeadLetterExchange() {
        return new DirectExchange("inventory.dlq.exchange");
    }

    // 绑定死信队列
    @Bean
    public Binding inventoryDeadLetterBinding() {
        return BindingBuilder.bind(inventoryDeadLetterQueue())
            .to(inventoryDeadLetterExchange())
            .with("inventory.dlq");
    }

    // 库存初始化队列
    @Bean
    public Queue inventoryInitQueue() {
        return QueueBuilder.durable("inventory.init.queue")
            .withArgument("x-dead-letter-exchange", "inventory.dlq.exchange")
            .build();
    }

    // 库存初始化队列绑定
    @Bean
    public Binding inventoryInitBinding() {
        return BindingBuilder.bind(inventoryInitQueue())
            .to(inventoryExchange())
            .with("inventory.init");
    }

    // 库存调整队列
    @Bean
    public Queue inventoryAdjustQueue() {
        return QueueBuilder.durable("inventory.adjust.queue")
            .withArgument("x-dead-letter-exchange", "inventory.dlq.exchange")
            .build();
    }

    // 库存调整队列绑定
    @Bean
    public Binding inventoryAdjustBinding() {
        return BindingBuilder.bind(inventoryAdjustQueue())
            .to(inventoryExchange())
            .with("inventory.adjust");
    }
}

