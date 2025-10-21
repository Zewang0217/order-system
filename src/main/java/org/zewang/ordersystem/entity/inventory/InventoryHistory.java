package org.zewang.ordersystem.entity.inventory;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:07
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import org.zewang.ordersystem.entity.BaseEntity;

@Data
@TableName("inventory_history")
public class InventoryHistory {

    @TableId(value = "history_id", type = IdType.AUTO)
    private Long historyId;

    @TableField(value = "product_id")
    private String productId;

    @TableField(value = "change_type")
    private String changeType;

    @TableField(value = "change_amount")
    private Integer changeAmount;

    @TableField(value = "before_stock")
    private Integer beforeStock;

    @TableField(value = "after_stock")
    private Integer afterStock;

    @TableField(value = "reference_id")
    private String referenceId;

    @TableField(value = "reason")
    private String reason;

    @TableField(value = "operator")
    private String operator;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public enum ChangeType {
        INCREMENT, DECREMENT, LOCK, RELEASE
    }
}
