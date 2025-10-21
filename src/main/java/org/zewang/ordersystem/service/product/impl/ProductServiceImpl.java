package org.zewang.ordersystem.service.product.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.dto.product.ProductCreateRequest;
import org.zewang.ordersystem.dto.product.ProductResponse;
import org.zewang.ordersystem.dto.product.ProductUpdateRequest;
import org.zewang.ordersystem.entity.product.Product;
import org.zewang.ordersystem.enums.ErrorCode;
import org.zewang.ordersystem.mapper.product.ProductMapper;
import org.zewang.ordersystem.service.product.ProductService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 09:59
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService {
    private final ProductMapper productMapper;

    // 创建产品
    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setProductId("PROD" + System.currentTimeMillis()); // 简单生成ID，实际项目中可使用更复杂的ID生成策略
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus());

        LocalDateTime now = LocalDateTime.now();
        product.setCreateTime(now);
        product.setUpdateTime(now);

        // 保存到数据库
        productMapper.insert(product);

        // 转换为响应对象
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setStatus(product.getStatus());
        response.setCreateTime(product.getCreateTime());
        response.setUpdateTime(product.getUpdateTime());

        log.info("创建产品: {}", product.getProductId());
        return response;
    }

    // 获取所有产品
    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productMapper.selectList(null);
        return products.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    // 根据ID获取产品
    @Override
    public ProductResponse getProductById(String productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return convertToResponse(product);    }

    // 更新产品
    public ProductResponse updateProduct(String productId, ProductUpdateRequest request) {
        Product existingProduct = this.findProductById(productId);

        // 更新产品信息
        existingProduct.setProductName(request.getProductName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setImageUrl(request.getImageUrl());
        existingProduct.setStatus(request.getStatus());
        existingProduct.setUpdateTime(LocalDateTime.now());

        productMapper.updateById(existingProduct);

        log.info("更新产品: {}", productId);
        return convertToResponse(existingProduct);
    }

    // 删除产品
    public void deleteProduct(String productId) {
        Product product = this.findProductById(productId);
        productMapper.deleteById(productId);
        log.info("删除产品: {}", productId);
    }

    // 根据ID查找产品实体类
    public Product findProductById(String productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        return product;
    }


    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setStatus(product.getStatus());
        response.setCreateTime(product.getCreateTime());
        response.setUpdateTime(product.getUpdateTime());
        return response;
    }

}
