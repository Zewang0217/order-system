package org.zewang.ordersystem.dto.user;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:25
 */

@Data
public class UserLoginResponse {
    private String token;
    private UserInfo userInfo;

    @Data
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;
        private String role;
    }

}
