package org.zewang.ordersystem.common.exception;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/26 22:45
 */

@Data
public class MessageProcessingException extends RuntimeException {
    private final Long messageId;
    private final String topic;

    public MessageProcessingException(Long messageId, String topic, String message, Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
        this.topic = topic;
    }

}
