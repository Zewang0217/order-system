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
 * @date 2025/10/20 13:56
 */

@Data
@TableName("roles")
public class Role {
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @TableField("role_name")
    private String roleName;

    @TableField("description")
    private String description;
}