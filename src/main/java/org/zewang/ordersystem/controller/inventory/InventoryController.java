package org.zewang.ordersystem.controller.inventory;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.dto.inventory.DeadLetterRetryRequest;
import org.zewang.ordersystem.dto.inventory.DeadLetterRetryResponse;
import org.zewang.ordersystem.dto.inventory.InventoryAdjustRequest;
import org.zewang.ordersystem.dto.inventory.InventoryAdjustResponse;
import org.zewang.ordersystem.dto.inventory.InventoryDeductByOrderRequest;
import org.zewang.ordersystem.dto.inventory.InventoryDeductRequest;
import org.zewang.ordersystem.dto.inventory.InventoryDeductResponse;
import org.zewang.ordersystem.dto.inventory.InventoryRequest;
import org.zewang.ordersystem.dto.inventory.InventoryResponse;
import org.zewang.ordersystem.service.inventory.InventoryService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 19:50
 */

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/init")
    public ApiResult<InventoryResponse> initializeInventory(@RequestBody InventoryRequest request) {
        InventoryResponse response = inventoryService.initializeInventory(request.getProductId(),
            request.getStock(), request.getUnit());
        return ApiResult.success(200, response);
    }

    @PostMapping("/deduct")
    public ApiResult<InventoryDeductResponse> deductInventory(@RequestBody InventoryDeductByOrderRequest request) {
        InventoryDeductResponse response = inventoryService.deductInventory(
            request.getOrderId());
        return ApiResult.success(200, response);
    }

    @GetMapping("/{productId}")
    public ApiResult<InventoryResponse> getInventory(@PathVariable String productId) {
        InventoryResponse response = inventoryService.getInventory(productId);
        return ApiResult.success(200, response);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<InventoryAdjustResponse> adjustInventory(
        @PathVariable String productId,
        @RequestBody InventoryAdjustRequest request) {
        InventoryAdjustResponse response = inventoryService.adjustInventory(productId, request);
        return ApiResult.success(200, response);
    }

    @PutMapping("/retry-dlq")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<DeadLetterRetryResponse> retryDeadLetter(
        @RequestBody DeadLetterRetryRequest request) {
        DeadLetterRetryResponse response = inventoryService.retryDeadLetter(request);
        return ApiResult.success(200, response);
    }

}
