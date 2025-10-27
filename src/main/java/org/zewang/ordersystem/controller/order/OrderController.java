package org.zewang.ordersystem.controller.order;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.dto.order.CancelOrderResponse;
import org.zewang.ordersystem.dto.order.CreateOrderRequest;
import org.zewang.ordersystem.dto.order.CreateOrderResponse;
import org.zewang.ordersystem.dto.order.HealthCheckResponse;
import org.zewang.ordersystem.dto.order.OrderDetailResponse;
import org.zewang.ordersystem.dto.order.OrderPageResponse;
import org.zewang.ordersystem.dto.order.OrderStatusResponse;
import org.zewang.ordersystem.dto.order.PaySuccessRequest;
import org.zewang.ordersystem.dto.order.PaySuccessResponse;
import org.zewang.ordersystem.enums.ErrorCode;
import org.zewang.ordersystem.service.order.OrderService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 19:33
 */

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResult<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = orderService.createOrder(request);
        return ApiResult.success(200, response);
    }

    @GetMapping
    public ApiResult<OrderPageResponse> getOrders(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false, defaultValue = "1") Integer page,
        @RequestParam(required = false, defaultValue = "10") Integer size) {
        OrderPageResponse response = orderService.getOrders(userId, status, page, size);
        return ApiResult.success(200, response);
    }

    @GetMapping("/{orderId}")
    public ApiResult<OrderDetailResponse> getOrderDetail(@PathVariable String orderId) {
        OrderDetailResponse response = orderService.getOrderDetail(orderId);
        return ApiResult.success(200, response);
    }

    @PutMapping("/{orderId}/cancel")
    public ApiResult<CancelOrderResponse> cancelOrder(@PathVariable String orderId) {
        CancelOrderResponse response = orderService.cancelOrder(orderId);
        return ApiResult.success(200, response);
    }

    @PutMapping("/{orderId}/pay-success")
    public ApiResult<PaySuccessResponse> paySuccess(@PathVariable String orderId,
        @RequestBody PaySuccessRequest request) {
        PaySuccessResponse response = orderService.paySuccess(orderId, request);
        return ApiResult.success(200, response);
    }

    @GetMapping("/{orderId}/status")
    public ApiResult<OrderStatusResponse> getOrderStatus(@PathVariable String orderId) {
        OrderStatusResponse response = orderService.getOrderStatus(orderId);
        return ApiResult.success(200, response);
    }

    @GetMapping("/{orderId}/retry-publish")
    public ApiResult<Void> retryPublish(@PathVariable String orderId,
        @RequestParam String operator,
        @RequestParam String reason) {
        orderService.retryPublish(orderId, operator, reason);
        return ApiResult.success(200, null);
    }

    @GetMapping("/health")
    public ApiResult<HealthCheckResponse> checkHealth() {
        try {
            boolean isHealthy = orderService.isOrderServiceHealthy();

            HealthCheckResponse response = new HealthCheckResponse();
            response.setTimestamp(LocalDateTime.now().toString());

            return ApiResult.success(response);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "健康检查失败");
        }
    }

}