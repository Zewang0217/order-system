package org.zewang.ordersystem.service.order;


import org.zewang.ordersystem.dto.order.CancelOrderResponse;
import org.zewang.ordersystem.dto.order.CreateOrderRequest;
import org.zewang.ordersystem.dto.order.CreateOrderResponse;
import org.zewang.ordersystem.dto.order.OrderDetailResponse;
import org.zewang.ordersystem.dto.order.OrderPageResponse;
import org.zewang.ordersystem.dto.order.OrderStatusResponse;
import org.zewang.ordersystem.dto.order.PaySuccessRequest;
import org.zewang.ordersystem.dto.order.PaySuccessResponse;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 18:16
 */

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request);

    OrderPageResponse getOrders(Long userId, String status, Integer page, Integer size);

    OrderDetailResponse getOrderDetail(String orderId);

    CancelOrderResponse cancelOrder(String orderId);

    PaySuccessResponse paySuccess(String orderId, PaySuccessRequest request);

    OrderStatusResponse getOrderStatus(String orderId);

    void retryPublish(String orderId, String operator, String reason);

    boolean isOrderServiceHealthy();
}

