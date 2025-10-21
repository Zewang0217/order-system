package org.zewang.ordersystem.entity.user;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 13:55
 */

@Data
@TableName("permissions")
public class Permission {
    @TableId(value = "permission_id", type = IdType.AUTO)
    private Long permissionId;

    @TableField("permission_name")
    private String permissionName;

    @TableField("resource")
    private String resource;

    @TableField("action")
    private String action;
}
