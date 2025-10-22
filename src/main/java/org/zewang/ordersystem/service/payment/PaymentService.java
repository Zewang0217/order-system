package org.zewang.ordersystem.service.payment;


import org.zewang.ordersystem.dto.payment.*;
import org.zewang.ordersystem.entity.payment.Payment;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 18:11
 */

public interface PaymentService {
    /**
     * 创建支付请求
     */
    CreatePaymentResponse createPayment(CreatePaymentRequest request);

    /**
     * 查询支付状态
     */
    PaymentStatusResponse getPaymentStatus(String transactionId);

    /**
     * 处理支付回调
     */
    PaymentCallbackResponse handlePaymentCallback(PaymentCallbackRequest request);

    /**
     * 支付完成通知订单服务
     */
    PaymentCompleteResponse completePayment(String orderId, PaymentCompleteRequest request);

    /**
     * 重试Outbox消息
     */
    RetryOutboxResponse retryOutbox(RetryOutboxRequest request);

    /**
     * 根据订单ID获取支付信息
     */
    Payment getPaymentByOrderId(String orderId);
}
