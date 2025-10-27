package org.zewang.ordersystem.config;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 10:35
 */

public final class RabbitConstants {
    private RabbitConstants() {}

    // 订单
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_PROCESSING_QUEUE = "order.processing.queue";
    public static final String ORDER_PROCESSING_ROUTING_KEY = "order.processing";
    public static final String ORDER_DLQ_QUEUE = "order.dlq.queue";
    public static final String ORDER_DLQ_ROUTING_KEY = "order.dlq";
    public static final String ORDER_DLQ_EXCHANGE = "order.dlq.exchange";

    // 支付
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String PAYMENT_SUCCESS_QUEUE = "payment.success.queue";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";

    // 库存
    public static final String INVENTORY_EXCHANGE = "inventory.exchange";
    public static final String INVENTORY_DEDUCT_QUEUE = "inventory.deduct.queue";
    public static final String INVENTORY_DEDUCT_ROUTING_KEY = "inventory.deduct";
    public static final String INVENTORY_DLQ_EXCHANGE = "inventory.dlq.exchange";
    public static final String INVENTORY_DLQ_QUEUE = "inventory.dlq.queue";
    public static final String INVENTORY_DLQ_ROUTING_KEY = "inventory.dlq";
    public static final String INVENTORY_INIT_QUEUE = "inventory.init.queue";
    public static final String INVENTORY_INIT_ROUTING_KEY = "inventory.init";
    public static final String INVENTORY_ADJUST_QUEUE = "inventory.adjust.queue";
    public static final String INVENTORY_ADJUST_ROUTING_KEY = "inventory.adjust";

    // 通用
    public static final int OUTBOX_DEFAULT_MAX_RETRY = 3;
    public static final long OUTBOX_MAX_BACKOFF_SECONDS = 3600; // cap backoff to 1 hour
}