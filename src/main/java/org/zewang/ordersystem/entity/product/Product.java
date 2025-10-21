package org.zewang.ordersystem.entity.product;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/17 11:05
 */
import com.baomidou.mybatisplus.annotation.*;
import org.zewang.ordersystem.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("products")
public class Product extends BaseEntity {

    @TableId(value = "product_id", type = IdType.INPUT)
    private String productId;

    @TableField(value = "product_name")
    private String productName;

    @TableField(value = "description")
    private String description;

    @TableField(value = "price")
    private BigDecimal price;

    @TableField(value = "category")
    private String category;

    @TableField(value = "image_url")
    private String imageUrl;

    @TableField(value = "status")
    private String status;

    public enum ProductStatus {
        ON_SALE, OFF_SALE, DELETED
    }

}
