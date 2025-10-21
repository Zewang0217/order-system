package org.zewang.ordersystem.entity.mq;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:13
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("outbox_messages")
public class OutboxMessage {

    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    @TableField(value = "topic")
    private String topic;

    @TableField(value = "message_key")
    private String messageKey;

    @TableField(value = "message_body")
    private String messageBody;

    @TableField(value = "message_status")
    private String messageStatus;

    @TableField(value = "retry_count")
    private Integer retryCount;

    @TableField(value = "max_retry_count")
    private Integer maxRetryCount;

    @TableField(value = "last_retry_time")
    private LocalDateTime lastRetryTime;

    @TableField(value = "error_message")
    private String errorMessage;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "sent_time")
    private LocalDateTime sentTime;

    public enum MessageStatus {
        PENDING, SENT, FAILED
    }
}

