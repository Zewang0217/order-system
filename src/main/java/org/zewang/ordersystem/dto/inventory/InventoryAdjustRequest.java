package org.zewang.ordersystem.dto.inventory;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 18:40
 */

// 库存调整请求DTO
@Data
public class InventoryAdjustRequest {
    private String adjustType; // increase/decrease
    private Integer amount;
    private String reason;
}

