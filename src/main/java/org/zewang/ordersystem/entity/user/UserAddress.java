package org.zewang.ordersystem.entity.user;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:02
 */

@Data
@TableName("user_address")
public class UserAddress {

    @TableId(value = "address_id", type = IdType.AUTO)
    private Long addressId;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "recipient_id")
    private String recipientId;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "province")
    private String province;

    @TableField(value = "city")
    private String city;

    @TableField(value = "district")
    private String district;

    @TableField(value = "detail_address")
    private String detailAddress;

    @TableField(value = "is_default")
    private Boolean isDefault;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
