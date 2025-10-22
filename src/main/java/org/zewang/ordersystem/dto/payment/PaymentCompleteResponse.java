// 支付完成响应DTO
package org.zewang.ordersystem.dto.payment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentCompleteResponse {
    private String orderId;
    private String status;
    private LocalDateTime confirmTime;
}
