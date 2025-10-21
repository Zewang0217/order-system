package org.zewang.ordersystem.entity.order;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:10
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_items")
public class OrderItem {

    @TableId(value = "item_id", type = IdType.AUTO)
    private Long itemId;

    @TableField(value = "order_id")
    private String orderId;

    @TableField(value = "product_id")
    private String productId;

    @TableField(value = "product_name")
    private String productName;

    @TableField(value = "quantity")
    private Integer quantity;

    @TableField(value = "unit_price")
    private BigDecimal unitPrice;

    @TableField(value = "total_price")
    private BigDecimal totalPrice;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

