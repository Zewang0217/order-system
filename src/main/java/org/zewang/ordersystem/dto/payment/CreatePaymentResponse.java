package org.zewang.ordersystem.dto.payment;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 18:05
 */

@Data
public class CreatePaymentResponse {
    private String transactionId;
    private String orderId;
    private BigDecimal amount;
    private String paymentType;
    private String status;
    private String payUrl;
    private LocalDateTime createTime;
}
