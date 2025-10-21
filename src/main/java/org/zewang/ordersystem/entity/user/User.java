package org.zewang.ordersystem.entity.user;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 10:57
 */

import com.baomidou.mybatisplus.annotation.*;
import org.zewang.ordersystem.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
public class User extends BaseEntity {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField(value = "username")
    private String username;

    @TableField(value = "password")
    private String password;

    @TableField(value = "email")
    private String email;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "address")
    private String address;

    @TableField(value = "role")
    private String role;

    @TableField(value = "status")
    private String status;

    public enum UserRole {
        USER, ADMIN
    }

    public enum UserStatus {
        ACTIVE, INACTIVE
    }
}