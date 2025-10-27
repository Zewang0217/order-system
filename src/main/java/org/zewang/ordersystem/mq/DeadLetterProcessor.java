package org.zewang.ordersystem.mq;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zewang.ordersystem.config.RabbitConstants;
import org.zewang.ordersystem.entity.mq.DeadLetterMessage;
import org.zewang.ordersystem.entity.mq.OutboxMessage;
import org.zewang.ordersystem.mapper.inventory.DeadLetterMapper;
import org.zewang.ordersystem.mapper.mq.OutboxMapper;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/23 20:11
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterProcessor {

    private final OutboxMapper outboxMapper;
    private final DeadLetterMapper deadLetterMapper;

    // 定期检查并转移失败的消息到死信队列列表，每分钟依次
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    @Transactional
    public void moveToDeadLetterQueue() {
        try {
            // 查找所有标记为FAILED的消息
            QueryWrapper<OutboxMessage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("message_status", "FAILED");

            outboxMapper.selectList(queryWrapper).forEach(this::transferToDeadLetter);
        } catch (Exception e) {
            log.error("转移失败消息到死信队列时出错", e);        }
    }

    // 转移消息到死信队列
    private void transferToDeadLetter(OutboxMessage outboxMessage) {
        DeadLetterMessage deadLetter = new DeadLetterMessage();
        try{
            // 创建死信消息
            deadLetter.setTopic(outboxMessage.getTopic());
            deadLetter.setMessageKey(outboxMessage.getMessageKey());
            deadLetter.setMessageBody(outboxMessage.getMessageBody());
            deadLetter.setFailureReason(outboxMessage.getErrorMessage());
            deadLetter.setRetryCount(outboxMessage.getRetryCount());
            deadLetter.setCreateTime(outboxMessage.getCreatedTime());
            deadLetter.setLastRetryTime(outboxMessage.getLastRetryTime());

            // 插入死信队列
            deadLetterMapper.insert(deadLetter);

            // 删除原始消息
            outboxMapper.deleteById(outboxMessage.getMessageId());

            log.info("转移失败消息到死信队列成功: {}", outboxMessage.getMessageId());
        } catch (Exception e) {
            log.error("转移消息到死信队列失败: ID={}", outboxMessage.getMessageId(), e);
            // 更新重试次数
            deadLetter.setRetryCount(deadLetter.getRetryCount() + 1);
            deadLetter.setLastRetryTime(LocalDateTime.now());
            deadLetterMapper.updateById(deadLetter);
        }

    }

    // 定时处理死信队列中的消息重试
    @Scheduled(cron = "0 */5 * * * ?") // 每5分钟执行一次
    @Transactional
    public void retryDeadLetterMessages() {
        try {
            QueryWrapper<DeadLetterMessage> queryWrapper = new QueryWrapper<>();
            queryWrapper.lt("retry_count", 5); // 限制最大重试次数

            deadLetterMapper.selectList(queryWrapper).forEach(this::retryDeadLetterMessage);
        } catch (Exception e) {
            log.error("重试死信消息时出错", e);
        }
    }

    private void retryDeadLetterMessage(DeadLetterMessage deadLetter) {
        try {
            // 创建新的outbox消息
            OutboxMessage outboxMessage = new OutboxMessage();
            outboxMessage.setTopic(deadLetter.getTopic());
            outboxMessage.setMessageKey(deadLetter.getMessageKey());
            outboxMessage.setMessageBody(deadLetter.getMessageBody());
            outboxMessage.setMessageStatus(OutboxMessage.MessageStatus.PENDING.name());
            outboxMessage.setRetryCount(0);
            outboxMessage.setMaxRetryCount(RabbitConstants.OUTBOX_DEFAULT_MAX_RETRY);
            outboxMessage.setCreatedTime(LocalDateTime.now());

            // 插入到outbox表中
            outboxMapper.insert(outboxMessage);

            // 更新死信消息状态
            deadLetter.setRetryCount(deadLetter.getRetryCount() + 1);
            deadLetter.setLastRetryTime(LocalDateTime.now());
            deadLetterMapper.updateById(deadLetter);

            log.info("死信消息已重新投递: DLQ ID={}, New Outbox ID={}",
                deadLetter.getDlqId(), outboxMessage.getMessageId());
        } catch (Exception e) {
            log.error("重试死信消息失败: DLQ ID={}", deadLetter.getDlqId(), e);
            // 更新重试次数
            deadLetter.setRetryCount(deadLetter.getRetryCount() + 1);
            deadLetter.setLastRetryTime(LocalDateTime.now());
            deadLetterMapper.updateById(deadLetter);
        }
    }
}
