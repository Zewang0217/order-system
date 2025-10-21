package org.zewang.ordersystem.dto.order;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 18:11
 */

@Data
public class PaySuccessRequest {
    private String transactionId;
    private String paymentTime;
}
