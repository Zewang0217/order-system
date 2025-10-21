package org.zewang.ordersystem.entity.payment;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:12
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("payment_callbacks")
public class PaymentCallback {

    @TableId(value = "callback_id", type = IdType.AUTO)
    private Long callbackId;

    @TableField(value = "transaction_id")
    private String transactionId;

    @TableField(value = "callback_data")
    private String callbackData;

    @TableField(value = "processed")
    private Boolean processed;

    @TableField(value = "process_result")
    private String processResult;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

