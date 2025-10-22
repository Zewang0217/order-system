package org.zewang.ordersystem.dto.payment;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 18:07
 */

@Data
public class PaymentCompleteRequest {
    private String transactionId;
    private LocalDateTime paymentTime;
}
