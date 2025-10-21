package org.zewang.ordersystem.dto.product;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 09:51
 */


import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductCreateRequest {

    private String productName;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private String status;
}