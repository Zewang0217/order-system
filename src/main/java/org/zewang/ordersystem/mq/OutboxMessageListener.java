package org.zewang.ordersystem.mq;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zewang.ordersystem.common.exception.MessageProcessingException;
import org.zewang.ordersystem.config.RabbitConstants;
import org.zewang.ordersystem.entity.mq.OutboxMessage;
import org.zewang.ordersystem.mapper.mq.OutboxMapper;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 处理Outbox消息的监听器，定时扫描并发送待处理消息到RabbitMQ * @email "Zewang0217@outlook.com"
 * @date 2025/10/23 20:00
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxMessageListener {

    private final OutboxMapper outboxMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 定时扫描并处理outbox中的信息，每10秒执行依次
    @Transactional
    @Scheduled(fixedDelayString = "10000")
    public void processPendingMessaged() {
        try {
            // 查询待处理消息 （最多100条）
            QueryWrapper<OutboxMessage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("message_status", "PENDING")
                .orderByAsc("created_time")
                .last("LIMIT 100");

            List<OutboxMessage> pendingMessages = outboxMapper.selectList(queryWrapper);

            for (OutboxMessage message : pendingMessages) {
                processMessage(message);
            }
        } catch (Exception e) {
            log.error("处理outbox消息时发生错误", e);
        }
    }

    // 处理单个消息
    private void processMessage(OutboxMessage message) {
        try {
            // 幂等性检查：检查消息是否已被处理
            if (isMessageProcessed(message.getMessageId())) {
                log.info("消息已处理过，跳过处理: ID={}, Topic={}", message.getMessageId(), message.getTopic());
                message.setMessageStatus("SENT");
                message.setSentTime(LocalDateTime.now());
                outboxMapper.updateById(message);
                return;
            }

            // 发送消息到 RabbitMQ并等待
            CorrelationData correlationData = new CorrelationData(message.getMessageId().toString());
            rabbitTemplate.convertAndSend(
                resolveExchangeByTopic(message.getTopic()), // 使用解析后的交换机名称
                message.getTopic(), // 使用 topic 作为 routing key
                message.getMessageBody(),
                correlationData
            );

            // 更新消息状态为已发送
            message.setMessageStatus("SENT");
            message.setSentTime(LocalDateTime.now());
            outboxMapper.updateById(message);

            log.info("成功发送消息: ID={}, Topic={}", message.getMessageId(), message.getTopic());
        } catch (Exception e) {
            log.error("发送消息失败: ID={}, Topic={}", message.getMessageId(), message.getTopic(), e);

            // 封装为统一异常处理
            MessageProcessingException mpException = new MessageProcessingException(
                message.getMessageId(),
                message.getTopic(),
                "消息发送失败",
                e
            );

            // 更新重试次数和状态
            updateMessageRetryStatus(message, e);
            // 重新抛出异常，由全局处理器统一处理
            throw mpException;
         }
    }

    private void updateMessageRetryStatus(OutboxMessage message, Exception e) {
        try {
            // 更新重试次数
            int retryCount = message.getRetryCount() + 1;
            message.setRetryCount(retryCount);
            message.setLastRetryTime(LocalDateTime.now());

            // 如果超过最大重试次数，标记为失败
            if (retryCount >= message.getMaxRetryCount()) {
                message.setMessageStatus("FAILED");
                message.setErrorMessage(truncateErrorMessage(e.getMessage(), 500));
            }

            outboxMapper.updateById(message);
        } catch (Exception updateException) {
            log.error("更新消息重试状态失败: ID={}", message.getMessageId(), updateException);        }
    }

    private boolean isMessageProcessed(Long messageId) {
        OutboxMessage message = outboxMapper.selectById(messageId);
        return message != null && "SENT".equals(message.getMessageStatus());
    }

    private String truncateErrorMessage(String message, int maxLength) {
        if (message == null) return "";
        return message.length() > maxLength ? message.substring(0, maxLength) : message;
    }

    private String resolveExchangeByTopic(String topic) {
        if (topic == null) return RabbitConstants.ORDER_EXCHANGE;
        if (topic.startsWith("inventory.")) {
            return RabbitConstants.INVENTORY_EXCHANGE;
        } else if (topic.startsWith("order.")) {
            return RabbitConstants.ORDER_EXCHANGE;
        } else if (topic.startsWith("payment.")) {
            return RabbitConstants.PAYMENT_EXCHANGE;
        }
        // 默认路由到订单交换机
        return RabbitConstants.ORDER_EXCHANGE;
    }
}
