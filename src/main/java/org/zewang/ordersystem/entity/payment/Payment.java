package org.zewang.ordersystem.entity.payment;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:11
 */

import com.baomidou.mybatisplus.annotation.*;
import org.zewang.ordersystem.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payments")
public class Payment extends BaseEntity {

    @TableId(value = "transaction_id", type = IdType.INPUT)
    private String transactionId;

    @TableField(value = "order_id")
    private String orderId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "amount")
    private BigDecimal amount;

    @TableField(value = "payment_type")
    private String paymentType;

    @TableField(value = "status")
    private String status;

    @TableField(value = "pay_url")
    private String payUrl;

    @TableField(value = "callback_url")
    private String callbackUrl;

    @TableField(value = "third_party_trade_no")
    private String thirdPartyTradeNo;

    @TableField(value = "payment_time")
    private LocalDateTime paymentTime;

    public enum PaymentStatus {
        PENDING, PAID, FAILED, CANCELLED, REFUNDED
    }
}

