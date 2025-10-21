package org.zewang.ordersystem.entity.fulfillment;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:13
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("shipment_tracks")
public class ShipmentTrack {

    @TableId(value = "track_id", type = IdType.AUTO)
    private Long trackId;

    @TableField(value = "fulfillment_id")
    private String fulfillmentId;

    @TableField(value = "location")
    private String location;

    @TableField(value = "description")
    private String description;

    @TableField(value = "track_time")
    private LocalDateTime trackTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
