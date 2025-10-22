// 重试Outbox响应DTO
package org.zewang.ordersystem.dto.payment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RetryOutboxResponse {
    private Integer retriedCount;
    private Integer successCount;
    private Integer failCount;
    private LocalDateTime lastRetryTime;
}
