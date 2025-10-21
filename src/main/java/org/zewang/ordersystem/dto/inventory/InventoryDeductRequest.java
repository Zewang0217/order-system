package org.zewang.ordersystem.dto.inventory;


import java.util.List;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 18:39
 */

@Data
public class InventoryDeductRequest {
    private String orderId;
    private List<InventoryDeductItem> items;

    @Data
    public static class InventoryDeductItem {
        private String productId;
        private Integer quantity;
    }
}
