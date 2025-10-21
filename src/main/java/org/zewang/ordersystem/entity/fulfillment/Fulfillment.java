package org.zewang.ordersystem.entity.fulfillment;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:12
 */

import com.baomidou.mybatisplus.annotation.*;
import org.zewang.ordersystem.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fulfillments")
public class Fulfillment extends BaseEntity {

    @TableId(value = "fulfillment_id", type = IdType.INPUT)
    private String fulfillmentId;

    @TableField(value = "order_id")
    private String orderId;

    @TableField(value = "carrier")
    private String carrier;

    @TableField(value = "tracking_number")
    private String trackingNumber;

    @TableField(value = "status")
    private String status;

    @TableField(value = "shipping_address")
    private String shippingAddress;

    @TableField(value = "estimated_delivery")
    private LocalDateTime estimatedDelivery;

    @TableField(value = "actual_delivery")
    private LocalDateTime actualDelivery;

    public enum FulfillmentStatus {
        PENDING, SHIPPED, IN_TRANSIT, DELIVERED, FAILED
    }
}

