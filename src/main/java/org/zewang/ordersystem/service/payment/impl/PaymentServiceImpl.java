package org.zewang.ordersystem.service.payment.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.entity.mq.OutboxMessage;
import org.zewang.ordersystem.entity.order.Order;
import org.zewang.ordersystem.entity.payment.Payment;
import org.zewang.ordersystem.entity.payment.Payment.PaymentStatus;
import org.zewang.ordersystem.enums.ErrorCode;
import org.zewang.ordersystem.mapper.mq.OutboxMapper;
import org.zewang.ordersystem.mapper.order.OrderMapper;
import org.zewang.ordersystem.mapper.payment.PaymentMapper;
import org.zewang.ordersystem.service.order.OrderService;
import org.zewang.ordersystem.service.payment.PaymentService;
import org.zewang.ordersystem.dto.payment.*;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 18:12
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;
    private final OutboxMapper outboxMapper;
    private final OrderService orderService;

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        Order order = orderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 检查订单是否可以支付
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PAID_ORDER, "订单状态不支持支付");
        }

        // 检查是否已存在支付记录
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", request.getOrderId());
        if (paymentMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.PAYMENT_CONFLICT, "该订单已有支付记录，请勿重复发起支付");
        }

        // 创建支付记录
        Payment payment = new Payment();
        String transactionId = "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        payment.setPaymentId(transactionId);
        payment.setUserId(request.getUserId());
        payment.setOrderId(request.getOrderId());
        payment.setAmount(order.getActualAmount());
        payment.setPaymentType(request.getPaymentType());
        payment.setStatus(Payment.PaymentStatus.PENDING.name());
        payment.setCallbackUrl(request.getCallbackUrl());

        // 模拟生成支付连接
        String payUrl = "https://openapi.alipay.com/pay?trade_no=" + transactionId;
        payment.setPayUrl(payUrl);

        paymentMapper.insert(payment);

        // 创建响应对象
        CreatePaymentResponse response = new CreatePaymentResponse();
        response.setTransactionId(transactionId);
        response.setOrderId(request.getOrderId());
        response.setAmount(order.getActualAmount());
        response.setPaymentType(request.getPaymentType());
        response.setStatus(Payment.PaymentStatus.PENDING.name());
        response.setPayUrl(payUrl);
        response.setCreateTime(LocalDateTime.now());

        return response;
    }

    @Override
    public PaymentStatusResponse getPaymentStatus(String transactionId) {
        Payment payment = paymentMapper.selectById(transactionId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        PaymentStatusResponse response = new PaymentStatusResponse();
        response.setTransactionId(payment.getPaymentId());
        response.setOrderId(payment.getOrderId());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setPaymentType(payment.getPaymentType());
        response.setPaymentTime(payment.getPaymentTime());
        response.setUpdateTime(payment.getUpdateTime());

        return response;
    }

    @Override
    @Transactional
    public PaymentCallbackResponse handlePaymentCallback(PaymentCallbackRequest request) {
        Payment payment = paymentMapper.selectById(request.getTransactionId());
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }

        // 验证签名（此处简化处理）
        if (!verifySignature(request)) {
            throw new BusinessException(ErrorCode.INVALID_SIGNATURE, "签名验证失败");
        }

        // 更新状态
        payment.setStatus(PaymentStatus.PAID.name());
        payment.setPaymentTime(LocalDateTime.now());
        paymentMapper.updateById(payment);

        // 创建outbox消息通知异步订单服务
        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setTopic("payment.success");
        outboxMessage.setMessageKey(payment.getOrderId());
        outboxMessage.setMessageBody(createPaymentSuccessMessage(payment));
        outboxMessage.setMessageStatus(OutboxMessage.MessageStatus.PENDING.name());
        outboxMessage.setRetryCount(0);
        outboxMessage.setMaxRetryCount(3);
        outboxMessage.setCreatedTime(LocalDateTime.now());
        outboxMapper.insert(outboxMessage);

        PaymentCallbackResponse response = new PaymentCallbackResponse();
        response.setTransactionId(request.getTransactionId());
        response.setStatus(Payment.PaymentStatus.PAID.name());
        response.setEventPublished(true);

        return response;
    }

    @Override
    public PaymentCompleteResponse completePayment(String orderId, PaymentCompleteRequest request) {
        // 调用订单服务更新订单状态为已支付
        // 这里可以通过远程调用或者本地服务调用实现
        // 为了简化，我们假设订单服务会处理这个请求

        PaymentCompleteResponse response = new PaymentCompleteResponse();
        response.setOrderId(orderId);
        response.setStatus(Payment.PaymentStatus.PAID.name());
        response.setConfirmTime(LocalDateTime.now());

        return response;
    }

    @Override
    public RetryOutboxResponse retryOutbox(RetryOutboxRequest request) {
        // 这个功能已经在 OutboxPublisher 中实现
        // 可以在这里添加额外的日志或统计功能

        RetryOutboxResponse response = new RetryOutboxResponse();
        response.setRetriedCount(0);
        response.setSuccessCount(0);
        response.setFailCount(0);
        response.setLastRetryTime(LocalDateTime.now());

        return response;
    }

    @Override
    public Payment getPaymentByOrderId(String orderId) {
        QueryWrapper<Payment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        return paymentMapper.selectOne(queryWrapper);
    }

    private boolean verifySignature(PaymentCallbackRequest request) {
        // 简化签名验证逻辑
        // 实际项目中应该根据具体的签名算法进行验证
        return request.getSign() != null && !request.getSign().isEmpty();
    }

    private String createPaymentSuccessMessage(Payment payment) {
        return "{"
            + "\"transactionId\":\"" + payment.getPaymentId() + "\","
            + "\"orderId\":\"" + payment.getOrderId() + "\","
            + "\"amount\":" + payment.getAmount() + ","
            + "\"paymentTime\":\"" + (payment.getPaymentTime() != null ? payment.getPaymentTime().toString() : "") + "\""
            + "}";
    }
}
