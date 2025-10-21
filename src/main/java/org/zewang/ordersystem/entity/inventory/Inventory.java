package org.zewang.ordersystem.entity.inventory;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:06
 */

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.ser.Serializers.Base;
import lombok.Data;
import java.time.LocalDateTime;
import org.zewang.ordersystem.entity.BaseEntity;

@Data
@TableName("inventory")
public class Inventory {
    @TableId(value = "inventory_id", type = IdType.AUTO)
    private Long inventoryId;

    @TableField(value = "product_id")
    private String productId;

    @TableField(value = "available_stock")
    private Integer availableStock = 0; // 默认值

    @TableField(value = "locked_stock")
    private Integer lockedStock = 0; // 默认值

    @TableField(value = "total_stock")
    private Integer totalStock = 0; // 默认值

    @TableField(value = "unit")
    private String unit;

    @Version
    @TableField(value = "version")
    private Integer version = 0; // 默认版本号

    @TableField(value = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now(); // 默认时间
}
