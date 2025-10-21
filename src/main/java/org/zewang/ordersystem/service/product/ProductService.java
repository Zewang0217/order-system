package org.zewang.ordersystem.service.product;


import java.util.List;
import org.zewang.ordersystem.dto.product.ProductCreateRequest;
import org.zewang.ordersystem.dto.product.ProductResponse;
import org.zewang.ordersystem.dto.product.ProductUpdateRequest;
import org.zewang.ordersystem.entity.product.Product;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 09:54
 */

public interface ProductService {
    // 创建产品
    ProductResponse createProduct(ProductCreateRequest request);

    // 获取所有产品
    List<ProductResponse> getAllProducts();

    // 根据ID获取产品
    ProductResponse getProductById(String productId);

    // 更新产品
    ProductResponse updateProduct(String productId, ProductUpdateRequest request);

    // 删除产品
    void deleteProduct(String productId);

    // 根据ID查找产品实体类
    Product findProductById(String productId);
}
