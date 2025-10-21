package org.zewang.ordersystem.dto.order;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 18:06
 */

@Data
public class CreateOrderRequest {
    private Long userId;
    List<OrderItemRequest> items = new ArrayList<>();
    private String address;
    private String paymentType;
    private String note;

    @Data
    public static class OrderItemRequest {
        private String productId;
        private Integer quantity;
    }

}
