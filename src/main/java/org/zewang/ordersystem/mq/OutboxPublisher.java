package org.zewang.ordersystem.mq;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.zewang.ordersystem.config.RabbitConstants;
import org.zewang.ordersystem.entity.mq.OutboxMessage;
import org.zewang.ordersystem.mapper.mq.OutboxMapper;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 10:19
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxMapper outboxMapper;
    private final RabbitTemplate rabbitTemplate;

    private static final int BATCH_SIZE = 50;


    @Scheduled(fixedDelayString = "5000") // 定时执行，每五秒一次消息发布检查
    public void scheduledPublish() {
        try {
            List<OutboxMessage> pending = outboxMapper.selectPendingMessages(BATCH_SIZE,
                RabbitConstants.OUTBOX_DEFAULT_MAX_RETRY, RabbitConstants.OUTBOX_MAX_BACKOFF_SECONDS);
            for (OutboxMessage msg : pending) {
                processSingle(msg);
            }
        } catch (Exception e) {
            log.error("Outbox scheduled publish error", e);
        }
    }

    @Transactional
    protected void processSingle(OutboxMessage msg) {
        if (msg == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 如果消息状态既不是 PENDING 也不是 FAILED，则跳过
        String status = msg.getMessageStatus();
        if (status == null ||
            !(OutboxMessage.MessageStatus.PENDING.name().equals(status)
                || OutboxMessage.MessageStatus.FAILED.name().equals(status))) {
            log.debug("Skip outbox message id={} with status={}", msg.getMessageId(), status);
            return;
        }


        // 检查指数回退：如果 lastRetryTime 存在且回退未到，跳过
        Integer retryCount = msg.getRetryCount() == null ? 0 : msg.getRetryCount();
        LocalDateTime lastRetry = msg.getLastRetryTime() == null ? msg.getCreatedTime() : msg.getLastRetryTime();
        long backoffSeconds = (long) Math.pow(2, Math.min(retryCount, 20)); // cap by code below
        if (backoffSeconds > RabbitConstants.OUTBOX_MAX_BACKOFF_SECONDS) {
            backoffSeconds = RabbitConstants.OUTBOX_MAX_BACKOFF_SECONDS;
        }
        if (lastRetry != null && lastRetry.plus(backoffSeconds, ChronoUnit.SECONDS).isAfter(now) &&
            OutboxMessage.MessageStatus.FAILED.name().equals(status)) {
            log.debug("outbox id={} backoff not passed yet (lastRetry={}, backoff={}s)", msg.getMessageId(), lastRetry, backoffSeconds);
            return;
        }

        try {
            // 根据 topic 决定发送到哪个交换机（简单规则：以 topic 前缀判断）
            String exchange = resolveExchangeByTopic(msg.getTopic());

            // 创建带 ID 的 CorrelationData 以便跟踪消息
            CorrelationData correlationData = new CorrelationData(String.valueOf(msg.getMessageId()));
            rabbitTemplate.convertAndSend(exchange, msg.getTopic(), msg.getMessageBody(), correlationData);

            UpdateWrapper<OutboxMessage> successUw = new UpdateWrapper<>();
            successUw.eq("message_id", msg.getMessageId())
                .set("message_status", OutboxMessage.MessageStatus.SENT.name())
                .set("sent_time", now)
                .set("error_message", null)
                .set("last_retry_time", now);
            outboxMapper.update(null, successUw);

            log.info("Outbox message published id={}", msg.getMessageId());
        } catch (Exception ex) {
            log.error("Publish failed for outbox id={}, err={}", msg.getMessageId(), ex.getMessage());
            int currentRetry = msg.getRetryCount() == null ? 0 : msg.getRetryCount();
            int newRetry = currentRetry + 1;
            int maxRetry = msg.getMaxRetryCount() == null ? RabbitConstants.OUTBOX_DEFAULT_MAX_RETRY : msg.getMaxRetryCount();

            // 计算下一次回退秒数并限制
            long nextBackoff = (long) Math.pow(2, Math.min(newRetry, 20));
            if (nextBackoff > RabbitConstants.OUTBOX_MAX_BACKOFF_SECONDS) {
                nextBackoff = RabbitConstants.OUTBOX_MAX_BACKOFF_SECONDS;
            }

            String truncatedErr = ex.getMessage() == null ? "null" : (ex.getMessage().length() > 1000 ? ex.getMessage().substring(0, 1000) : ex.getMessage());

            UpdateWrapper<OutboxMessage> failUw = new UpdateWrapper<>();
            failUw.eq("message_id", msg.getMessageId())
                .set("retry_count", newRetry)
                .set("last_retry_time", now)
                .set("error_message", truncatedErr)
                .set("message_status", OutboxMessage.MessageStatus.FAILED.name());
            outboxMapper.update(null, failUw);

            if (newRetry >= maxRetry) {
                log.warn("Outbox id={} reached max retry {} and marked FAILED", msg.getMessageId(), maxRetry);
            }
        }
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

