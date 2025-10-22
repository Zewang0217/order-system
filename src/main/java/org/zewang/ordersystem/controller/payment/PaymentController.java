package org.zewang.ordersystem.controller.payment;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.dto.payment.*;
import org.zewang.ordersystem.service.payment.PaymentService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 18:35
 */

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 创建支付请求
    @PostMapping
    public ApiResult<CreatePaymentResponse> createPayment(@RequestBody CreatePaymentRequest request) {
        CreatePaymentResponse response = paymentService.createPayment(request);
        return ApiResult.success(200, response);
    }

    // 查询支付状态
    @GetMapping("/{transactionId}")
    public ApiResult<PaymentStatusResponse> getPaymentStatus(@PathVariable String transactionId) {
        PaymentStatusResponse response = paymentService.getPaymentStatus(transactionId);
        return ApiResult.success(200, response);
    }


    /**
     * 支付回调（异步）
     */
    @PostMapping("/callback")
    public ApiResult<PaymentCallbackResponse> handlePaymentCallback(@RequestBody PaymentCallbackRequest request) {
        PaymentCallbackResponse response = paymentService.handlePaymentCallback(request);
        return ApiResult.success(200, response);
    }

    /**
     * 支付成功事件转发（内部接口）
     */
    @PutMapping("/{orderId}/complete")
    public ApiResult<PaymentCompleteResponse> completePayment(
        @PathVariable String orderId,
        @RequestBody PaymentCompleteRequest request) {
        PaymentCompleteResponse response = paymentService.completePayment(orderId, request);
        return ApiResult.success(200, response);
    }

    /**
     * 重试支付事件发送（Outbox 补偿）
     */
    @PutMapping("/retry-outbox")
    public ApiResult<RetryOutboxResponse> retryOutbox(@RequestBody RetryOutboxRequest request) {
        RetryOutboxResponse response = paymentService.retryOutbox(request);
        return ApiResult.success(200, response);
    }
}
