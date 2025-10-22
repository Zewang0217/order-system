package org.zewang.ordersystem.entity.payment;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import lombok.Data;
import org.zewang.ordersystem.entity.BaseEntity;
import java.time.LocalDateTime;

@Data
@TableName("payments")
public class Payment extends BaseEntity {

    @TableId(value = "payment_id", type = IdType.INPUT)
    private String paymentId;

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

    @TableField(value = "callback_url")
    private String callbackUrl;

    @TableField(value = "pay_url")
    private String payUrl;

    @TableField(value = "payment_time")
    private LocalDateTime paymentTime;

    // 支付状态枚举
    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }

    // 支付类型枚举
    public enum PaymentType {
        ALIPAY, WECHAT_PAY, BANK_TRANSFER
    }

    @Override
    public String toString() {
        return "Payment{" +
            "paymentId='" + paymentId + '\'' +
            ", orderId='" + orderId + '\'' +
            ", amount=" + amount +
            ", status='" + status + '\'' +
            '}';
    }
}
