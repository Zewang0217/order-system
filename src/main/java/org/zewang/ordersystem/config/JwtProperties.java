package org.zewang.ordersystem.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:59
 */

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "Hj82kPZsT9dN4Lx1rB6fQw7mA2uVx5Cz";
    private Long expiration = 86400000L; // 24小时
    private String header = "Authorization";
    private String tokenPrefix = "Bearer ";
}
