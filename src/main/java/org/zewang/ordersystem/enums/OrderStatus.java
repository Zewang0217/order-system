package org.zewang.ordersystem.enums;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 09:58
 */

public enum OrderStatus {
    PENDING_PAYMENT,    // 待支付
    PAID,               // 已支付
    PROCESSING,         // 处理中
    SHIPPED,            // 已发货
    DELIVERED,          // 已送达
    CANCELLED,          // 已取消
    REFUNDED            // 已退款
}
