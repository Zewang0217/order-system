package org.zewang.ordersystem.controller.product;



import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.dto.product.ProductCreateRequest;
import org.zewang.ordersystem.dto.product.ProductResponse;
import org.zewang.ordersystem.dto.product.ProductUpdateRequest;
import org.zewang.ordersystem.service.product.ProductService;
/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 09:52
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 创建产品
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ApiResult.success(200, response);
    }

    // 获取所有产品
    @GetMapping
    public ApiResult<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ApiResult.success(200, products);
    }

    // 根据ID获取产品
    @GetMapping("/{productId}")
    public ApiResult<ProductResponse> getProductById(@PathVariable String productId) {
        ProductResponse product = productService.getProductById(productId);
        return ApiResult.success(200, product);
    }

    // 更新产品 - 仅管理员可操作
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<ProductResponse> updateProduct(
        @PathVariable String productId,
        @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ApiResult.success(200, response);
    }

    // 删除产品 - 仅管理员可操作
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ApiResult.success(200, null);
    }

}
