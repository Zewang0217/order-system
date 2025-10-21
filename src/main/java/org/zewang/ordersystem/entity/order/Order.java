package org.zewang.ordersystem.entity.order;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:08
 */

import com.baomidou.mybatisplus.annotation.*;
import org.zewang.ordersystem.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
public class Order extends BaseEntity {

    @TableId(value = "order_id", type = IdType.INPUT)
    private String orderId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    @TableField(value = "actual_amount")
    private BigDecimal actualAmount;

    @TableField(value = "payment_type")
    private String paymentType;

    @TableField(value = "status")
    private String status;

    @TableField(value = "address")
    private String address;

    @TableField(value = "note")
    private String note;

    @TableField(value = "paid_time")
    private LocalDateTime paidTime;

    @TableField(value = "cancelled_time")
    private LocalDateTime cancelledTime;

    public enum PaymentType {
        ALIPAY, WECHAT, CREDIT_CARD, BANK_TRANSFER
    }
}
