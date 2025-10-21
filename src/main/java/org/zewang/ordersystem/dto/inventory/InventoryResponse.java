package org.zewang.ordersystem.dto.inventory;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 18:40
 */

@Data
public class InventoryResponse {
    private String productId;
    private String productName;
    private Integer stock;
    private String unit;
    private LocalDateTime lastUpdated;
}
