package org.zewang.ordersystem.service.inventory;

import java.util.List;
import org.zewang.ordersystem.dto.inventory.DeadLetterRetryRequest;
import org.zewang.ordersystem.dto.inventory.DeadLetterRetryResponse;
import org.zewang.ordersystem.dto.inventory.InventoryAdjustRequest;
import org.zewang.ordersystem.dto.inventory.InventoryAdjustResponse;
import org.zewang.ordersystem.dto.inventory.InventoryDeductRequest;
import org.zewang.ordersystem.dto.inventory.InventoryDeductResponse;
import org.zewang.ordersystem.dto.inventory.InventoryResponse;

public interface InventoryService {
    // 初始化库存的方法
    public InventoryResponse initializeInventory(String productId, Integer initialStock, String unit);    // 扣减库存的方法
    // 扣减库存的方法
    InventoryDeductResponse deductInventory(String orderId);
    // 根据产品ID获取库存信息的方法
    InventoryResponse getInventory(String productId);
    // 调整库存的方法/
    InventoryAdjustResponse adjustInventory(String productId, InventoryAdjustRequest request);
    // 处理死信队列重试请求的方法
    DeadLetterRetryResponse retryDeadLetter(DeadLetterRetryRequest request);

}
