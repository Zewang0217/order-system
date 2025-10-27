package org.zewang.ordersystem.service.order.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.dto.fulfillment.FulfillmentCreateRequest;
import org.zewang.ordersystem.dto.order.CancelOrderResponse;
import org.zewang.ordersystem.dto.order.CreateOrderRequest;
import org.zewang.ordersystem.dto.order.CreateOrderResponse;
import org.zewang.ordersystem.dto.order.OrderDetailResponse;
import org.zewang.ordersystem.dto.order.OrderPageResponse;
import org.zewang.ordersystem.dto.order.OrderStatusResponse;
import org.zewang.ordersystem.dto.order.PaySuccessRequest;
import org.zewang.ordersystem.dto.order.PaySuccessResponse;
import org.zewang.ordersystem.entity.mq.OutboxMessage;
import org.zewang.ordersystem.entity.order.Order;
import org.zewang.ordersystem.entity.order.OrderItem;
import org.zewang.ordersystem.entity.product.Product;
import org.zewang.ordersystem.enums.ErrorCode;
import org.zewang.ordersystem.enums.OrderStatus;
import org.zewang.ordersystem.mapper.order.OrderItemMapper;
import org.zewang.ordersystem.mapper.order.OrderMapper;
import org.zewang.ordersystem.mapper.product.ProductMapper;
import org.zewang.ordersystem.mapper.mq.OutboxMapper;
import org.zewang.ordersystem.service.fulfillment.FulfillmentService;
import org.zewang.ordersystem.service.order.OrderService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 18:22
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final OrderItemMapper orderItemMapper;
    private final OutboxMapper outboxMapper;
    private final FulfillmentService fulfillmentService;

    @Override
    @Transactional // 添加事务
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        // 检查库存  （简化）
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
            }
        }

        // 创建订单
        Order order = new Order();
        String orderId = "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
            String.format("%04d", (int)(Math.random() * 10000));
        order.setOrderId(orderId);
        order.setUserId(request.getUserId());
        order.setAddress(request.getAddress());
        order.setPaymentType(request.getPaymentType());
        order.setStatus(OrderStatus.PENDING_PAYMENT.name());
        order.setNote(request.getNote());
        order.setCreateTime(LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setUpdateTime(now);

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productMapper.selectById(item.getProductId());
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        order.setTotalAmount(totalAmount);
        order.setActualAmount(totalAmount);



        orderMapper.insert(order);

        // 3. 创建订单项
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(itemRequest.getProductId());

            Product product = productMapper.selectById(itemRequest.getProductId());
            orderItem.setProductName(product.getProductName());

            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(orderItem.getUnitPrice().multiply(new BigDecimal(orderItem.getQuantity())));
            orderItem.setCreateTime(now);

            orderItemMapper.insert(orderItem);
        }


        // 4. 发送消息到outbox表（用于异步通知库存服务）
        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setTopic("order.created");
        outboxMessage.setMessageKey(orderId);
        outboxMessage.setMessageBody("{\"message\":\"Order created: " + orderId + "\",\"orderId\":\"" + orderId + "\"}");        outboxMessage.setMessageStatus(OutboxMessage.MessageStatus.PENDING.name());
        outboxMessage.setRetryCount(0);
        outboxMessage.setMaxRetryCount(3);
        outboxMessage.setCreatedTime(now);
        outboxMapper.insert(outboxMessage);

        log.info("订单创建成功：{}", orderId);

        // 构造返回结果
        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderId(orderId);
        response.setStatus(OrderStatus.PENDING_PAYMENT.name());
        response.setCreateTime(order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setTotalAmount(totalAmount);
        response.setPaymentType(request.getPaymentType());
        response.setCreatedAt(order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setUserId(request.getUserId());
        response.setAddress(request.getAddress());
        response.setNote(request.getNote());

        return response;
    }

    @Override
    public OrderPageResponse getOrders(Long userId, String status, Integer page, Integer size) {
        Page<Order> pageObj = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        IPage<Order> orderPage;

        if (userId != null || status != null) {
            orderPage = orderMapper.selectOrdersByUserIdAndStatus(pageObj, userId, status);
        } else {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            orderPage = orderMapper.selectPage(pageObj, queryWrapper);
        }

        OrderPageResponse response = new OrderPageResponse();
        response.setPage((int)orderPage.getCurrent());
        response.setSize((int)orderPage.getSize());
        response.setTotal(orderPage.getTotal());

        List<OrderPageResponse.OrderRecord> records = new ArrayList<>();
        for (Order order : orderPage.getRecords()) {
            OrderPageResponse.OrderRecord record = new OrderPageResponse.OrderRecord();
            record.setOrderId(order.getOrderId());
            record.setStatus(order.getStatus());
            if (order.getCreateTime() != null) {
                record.setCreatedAt(order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }
            records.add(record);
        }

        response.setRecords(records);

        return response;
    }

    @Override
    public OrderDetailResponse getOrderDetail(String orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        response.setAddress(order.getAddress());
        response.setPaymentType(order.getPaymentType());
        response.setTotalAmount(order.getTotalAmount().toString());
        response.setCreateTime(order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setUpdateTime(order.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        response.setUpdatedAt(order.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 查询订单详情
        List<OrderItem> orderItems = orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_id", orderId));
        List<OrderDetailResponse.OrderItemDetail> items = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderDetailResponse.OrderItemDetail item = new OrderDetailResponse.OrderItemDetail();
            item.setProductId(orderItem.getProductId());
            item.setProductName(orderItem.getProductName());
            item.setQuantity(orderItem.getQuantity());
            item.setPrice(orderItem.getUnitPrice().toString());
            items.add(item);
        }
        response.setItems(items);

        return response;
    }

    @Override
    @Transactional
    public CancelOrderResponse cancelOrder(String orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 检查订单状态
        if (OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PAID_ORDER);
        }

        // 更新状态
        order.setStatus(OrderStatus.CANCELLED.name());
        order.setCancelledTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 发送消息到outbox表（用于异步通知库存服务恢复库存）
        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setTopic("order.cancelled");
        outboxMessage.setMessageKey(orderId);
        outboxMessage.setMessageBody("{\"message\":\"Order cancelled: " + orderId + "\",\"orderId\":\"" + orderId + "\"}");        outboxMessage.setMessageStatus(OutboxMessage.MessageStatus.PENDING.name());
        outboxMessage.setMessageStatus(OutboxMessage.MessageStatus.PENDING.name());
        outboxMessage.setRetryCount(0);
        outboxMessage.setMaxRetryCount(3);
        outboxMessage.setCreatedTime(LocalDateTime.now());
        outboxMapper.insert(outboxMessage);

        log.info("订单已取消：{}", orderId);

        CancelOrderResponse response = new CancelOrderResponse();
        response.setOrderId(orderId);
        response.setStatus(OrderStatus.CANCELLED.name());

        return response;
    }

    @Override
    @Transactional
    public PaySuccessResponse paySuccess(String orderId, PaySuccessRequest request) {
        Order order = orderMapper.selectById(orderId);
        // 幂等性检查
        if (OrderStatus.PAID.name().equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PAID_ORDER);
        }
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 更新订单状态
        order.setStatus(OrderStatus.PAID.name());
        order.setPaidTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 创建发货单
        FulfillmentCreateRequest fulfillmentCreateRequest = new FulfillmentCreateRequest();
        fulfillmentCreateRequest.setOrderId(orderId);
        fulfillmentCreateRequest.setCarrier("默认承运商");
        fulfillmentCreateRequest.setTrackingNumber("");
        fulfillmentCreateRequest.setShippingAddress(order.getAddress());
        fulfillmentCreateRequest.setEstimatedDelivery(LocalDateTime.now().plusDays(3)); // 预计3天后送达

        fulfillmentService.createFulfillment(fulfillmentCreateRequest);

        // 发送消息到outbox表（用于异步通知发货服务）
        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setTopic("order.paid");
        outboxMessage.setMessageKey(orderId);
        outboxMessage.setMessageBody("{\"message\":\"Order paid: " + orderId + "\",\"orderId\":\"" + orderId + "\",\"transactionId\":\"" + request.getTransactionId() + "\"}");        outboxMessage.setMessageStatus(OutboxMessage.MessageStatus.PENDING.name());
        outboxMessage.setRetryCount(0);
        outboxMessage.setMaxRetryCount(3);
        outboxMessage.setCreatedTime(LocalDateTime.now());
        outboxMapper.insert(outboxMessage);
        log.info("订单支付成功: {}", orderId);


        PaySuccessResponse response = new PaySuccessResponse();
        response.setOrderId(orderId);
        response.setStatus(OrderStatus.PAID.name());

        return response;
    }

    @Override
    public OrderStatusResponse getOrderStatus(String orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        OrderStatusResponse response = new OrderStatusResponse();
        response.setOrderId(orderId);
        response.setStatus(order.getStatus());
        if (order.getUpdateTime() != null) {
            response.setUpdateTime(order.getUpdateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }        return response;
    }

    @Override
    public void retryPublish(String orderId, String operator, String reason) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 重新发送信息到outbox表
        OutboxMessage outboxMessage = new OutboxMessage();
        outboxMessage.setTopic("order.retry");
        outboxMessage.setMessageKey(orderId);
        outboxMessage.setMessageBody("{\"message\":\"Order retry: " + orderId + "\",\"orderId\":\"" + orderId + "\",\"operator\":\"" + operator + "\",\"reason\":\"" + reason + "\"}");        outboxMessage.setRetryCount(0);
        outboxMessage.setMaxRetryCount(3);
        outboxMessage.setCreatedTime(LocalDateTime.now());
        outboxMapper.insert(outboxMessage);

        log.info("订单消息重发并写入 outbox：{}, 操作人：{}, 原因：{}", orderId, operator, reason);
    }

    @Override
    public boolean isOrderServiceHealthy() {
        try {
            orderMapper.selectCount(null);
            return true;
        } catch (Exception e) {
            log.error("订单服务健康检查失败", e);
            return false;
        }
    }
}
