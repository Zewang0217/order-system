package org.zewang.ordersystem.entity.user;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 16:44
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("role_permissions")
public class RolePermission {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("role_id")
    private Long roleId;

    @TableField("permission_id")
    private Long permissionId;
}
