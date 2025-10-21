package org.zewang.ordersystem.dto.order;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 18:09
 */

import lombok.Data;
import java.util.List;

@Data
public class OrderDetailResponse {
    private String orderId;
    private Long userId;
    private List<OrderItemDetail> items;
    private String status;
    private String createdAt;
    private String address;
    private String paymentType;
    private String totalAmount;
    private String createTime;
    private String updateTime;
    private String updatedAt;

    @Data
    public static class OrderItemDetail {
        private String productId;
        private String productName;
        private Integer quantity;
        private String price;
    }
}