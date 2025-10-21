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
import java.time.LocalDateTime;

@Data
@TableName("order_status_log")
public class OrderStatusLog {

    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @TableField(value = "order_id")
    private String orderId;

    @TableField(value = "from_status")
    private String fromStatus;

    @TableField(value = "to_status")
    private String toStatus;

    @TableField(value = "remark")
    private String remark;

    @TableField(value = "operator")
    private String operator;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

