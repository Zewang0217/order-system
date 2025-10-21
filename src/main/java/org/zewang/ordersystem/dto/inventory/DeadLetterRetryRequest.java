package org.zewang.ordersystem.dto.inventory;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 18:41
 */

// 死信重试请求DTO
@Data
public class DeadLetterRetryRequest {
    private String messageId;
    private Integer maxRetry;
}
