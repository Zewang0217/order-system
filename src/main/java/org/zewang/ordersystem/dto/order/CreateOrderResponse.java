package org.zewang.ordersystem.dto.order;


import java.math.BigDecimal;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 18:08
 */

@Data
public class CreateOrderResponse {
    private String orderId;
    private String status;
    private String createTime;
    private BigDecimal totalAmount;
    private String paymentType;
    private String createdAt;
    private Long userId;
    private String address;
    private String note;

}
