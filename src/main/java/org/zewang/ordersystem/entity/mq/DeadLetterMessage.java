package org.zewang.ordersystem.entity.mq;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:14
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("dead_letter_messages")
public class DeadLetterMessage {

    @TableId(value = "dlq_id", type = IdType.AUTO)
    private Long dlqId;

    @TableField(value = "original_message_id")
    private Long originalMessageId;

    @TableField(value = "topic")
    private String topic;

    @TableField(value = "message_key")
    private String messageKey;

    @TableField(value = "message_body")
    private String messageBody;

    @TableField(value = "failure_reason")
    private String failureReason;

    @TableField(value = "retry_count")
    private Integer retryCount;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "last_retry_time")
    private LocalDateTime lastRetryTime;
}
