package org.zewang.ordersystem.mq;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/26 23:36
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMessageConsumer {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 处理订单创建消息 - 手动确认示例
     */
    @RabbitListener(queues = "${rabbitmq.queue.order.processing:order.processing.queue}", ackMode = "MANUAL")
    @Transactional
    public void handleOrderCreatedMessage(Message message, com.rabbitmq.client.Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String messageBody = new String(message.getBody());
            log.info("收到订单创建消息: {}", messageBody);

            // 处理业务逻辑
            // 这里可以添加具体的订单处理逻辑
            processOrderMessage(messageBody);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("订单创建消息处理成功并已确认: deliveryTag={}", deliveryTag);
        } catch (Exception e) {
            log.error("处理订单创建消息失败: deliveryTag={}", deliveryTag, e);
            try {
                // 拒绝消息并重新入队
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                log.error("拒绝消息失败: deliveryTag={}", deliveryTag, ex);
            }
        }
    }

    /**
     * 处理支付成功消息 - 手动确认示例
     */
    @RabbitListener(queues = "${rabbitmq.queue.payment.success:payment.success.queue}", ackMode = "MANUAL")
    @Transactional
    public void handlePaymentSuccessMessage(Message message, com.rabbitmq.client.Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String messageBody = new String(message.getBody());
            log.info("收到支付成功消息: {}", messageBody);

            // 处理业务逻辑
            processPaymentMessage(messageBody);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            log.info("支付成功消息处理成功并已确认: deliveryTag={}", deliveryTag);
        } catch (Exception e) {
            log.error("处理支付成功消息失败: deliveryTag={}", deliveryTag, e);
            try {
                // 拒绝消息并不重新入队（转移到死信队列）
                channel.basicNack(deliveryTag, false, false);
            } catch (Exception ex) {
                log.error("拒绝消息失败: deliveryTag={}", deliveryTag, ex);
            }
        }
    }

    private void processOrderMessage(String messageBody) throws IOException {
        // 模拟处理订单消息
        log.info("处理订单消息: {}", messageBody);
        // 实际业务逻辑处理...
    }

    private void processPaymentMessage(String messageBody) throws IOException {
        // 模拟处理支付消息
        log.info("处理支付消息: {}", messageBody);
        // 实际业务逻辑处理...
    }}
