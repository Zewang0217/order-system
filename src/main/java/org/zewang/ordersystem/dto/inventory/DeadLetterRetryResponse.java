package org.zewang.ordersystem.dto.inventory;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 18:41
 */

// 死信重试响应DTO
@Data
public class DeadLetterRetryResponse {
    private String messageId;
    private String status;
    private Integer retryCount;
    private String message;
}