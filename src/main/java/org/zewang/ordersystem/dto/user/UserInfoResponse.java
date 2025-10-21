package org.zewang.ordersystem.dto.user;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:23
 */

@Data
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createTime;

}
