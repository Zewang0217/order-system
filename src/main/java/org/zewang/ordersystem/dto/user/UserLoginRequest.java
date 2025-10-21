package org.zewang.ordersystem.dto.user;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:22
 */

@Data
public class UserLoginRequest {
    private String username;
    private String password;

}
