package org.zewang.ordersystem.dto.inventory;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 21:05
 */

import lombok.Data;
import java.util.List;

@Data
public class InventoryDeductResponse {
    private String orderId;
    private List<DeductItemInfo> items;

    @Data
    public static class DeductItemInfo {
        private String productId;
        private String productName;
        private Integer quantity;
        private Integer remainingStock;
    }
}