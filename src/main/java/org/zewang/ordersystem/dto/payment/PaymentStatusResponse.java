package org.zewang.ordersystem.dto.payment;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 18:06
 */

@Data
public class PaymentStatusResponse {
    private String transactionId;
    private String orderId;
    private String status;
    private BigDecimal amount;
    private String paymentType;
    private LocalDateTime paymentTime;
    private LocalDateTime updateTime;
}
