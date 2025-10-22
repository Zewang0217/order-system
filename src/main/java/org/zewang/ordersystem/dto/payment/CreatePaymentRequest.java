package org.zewang.ordersystem.dto.payment;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 18:05
 */

@Data
public class CreatePaymentRequest {
    private String orderId;
    private String paymentType;
    private String callbackUrl;
    private Long userId;
}
