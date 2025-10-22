// 重试Outbox请求DTO
package org.zewang.ordersystem.dto.payment;

import lombok.Data;

@Data
public class RetryOutboxRequest {
    private String operator;
    private Integer maxRetryCount;
    private String filterStatus;
}
