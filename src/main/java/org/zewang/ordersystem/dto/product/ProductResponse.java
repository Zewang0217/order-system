package org.zewang.ordersystem.dto.product;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 09:52
 */


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponse {
    private String productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}