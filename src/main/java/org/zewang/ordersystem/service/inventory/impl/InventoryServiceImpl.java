package org.zewang.ordersystem.service.inventory.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.dto.inventory.DeadLetterRetryRequest;
import org.zewang.ordersystem.dto.inventory.DeadLetterRetryResponse;
import org.zewang.ordersystem.dto.inventory.InventoryAdjustRequest;
import org.zewang.ordersystem.dto.inventory.InventoryAdjustResponse;
import org.zewang.ordersystem.dto.inventory.InventoryDeductRequest;
import org.zewang.ordersystem.dto.inventory.InventoryDeductRequest.InventoryDeductItem;
import org.zewang.ordersystem.dto.inventory.InventoryDeductResponse;
import org.zewang.ordersystem.dto.inventory.InventoryResponse;
import org.zewang.ordersystem.entity.inventory.Inventory;
import org.zewang.ordersystem.entity.inventory.InventoryHistory;
import org.zewang.ordersystem.entity.mq.DeadLetterMessage;
import org.zewang.ordersystem.entity.order.Order;
import org.zewang.ordersystem.entity.order.OrderItem;
import org.zewang.ordersystem.entity.product.Product;
import org.zewang.ordersystem.enums.ErrorCode;
import org.zewang.ordersystem.mapper.inventory.DeadLetterMapper;
import org.zewang.ordersystem.mapper.inventory.InventoryMapper;
import org.zewang.ordersystem.mapper.inventory.InventoryHistoryMapper;
import org.zewang.ordersystem.mapper.order.OrderItemMapper;
import org.zewang.ordersystem.mapper.order.OrderMapper;
import org.zewang.ordersystem.mapper.product.ProductMapper;
import org.zewang.ordersystem.service.inventory.InventoryService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/21 19:14
 */

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final InventoryHistoryMapper historyMapper;
    private final DeadLetterMapper deadLetterMapper;
    private final RabbitTemplate rabbitTemplate;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public InventoryResponse initializeInventory(String productId, Integer initialStock, String unit) {
        Inventory existingInventory = inventoryMapper.selectById(productId);
        if (existingInventory != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR,
                "产品 " + productId + " 的库存已存在");
        }

        // 创建新的库存记录
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableStock(initialStock);
        inventory.setTotalStock(initialStock);
        inventory.setLockedStock(0);
        inventory.setUnit(unit);
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryMapper.insert(inventory);

        // 记录初始化历史
        InventoryHistory history = new InventoryHistory();
        history.setProductId(productId);
        history.setChangeType(InventoryHistory.ChangeType.INCREMENT.name());
        history.setChangeAmount(initialStock);
        history.setBeforeStock(0);
        history.setAfterStock(initialStock);
        history.setOperator("initializeInventory");
        LocalDateTime now = LocalDateTime.now();
        history.setCreateTime(now);
        history.setReason("库存初始化");
        historyMapper.insert(history);

        // 构造返回结果
        InventoryResponse response = new InventoryResponse();
        response.setProductId(productId);
        response.setStock(initialStock);
        response.setUnit(unit);
        response.setLastUpdated(inventory.getLastUpdated());
        return response;

    }

    @Override
    @Transactional
    public InventoryDeductResponse deductInventory(String orderId) {
        InventoryDeductResponse response = new InventoryDeductResponse();
        response.setOrderId(orderId);

        Order order = orderMapper.selectById(orderId);
        if (order == null)
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);

        List<OrderItem> orderItems = orderItemMapper.selectList(
            new QueryWrapper<OrderItem>().eq("order_id", orderId)
        );

        if (orderItems.isEmpty())
            throw new BusinessException(ErrorCode.PARAM_ERROR, "订单无商品项");


        List<InventoryDeductRequest.InventoryDeductItem> deductItems = new ArrayList<>();
        List<InventoryDeductResponse.DeductItemInfo> deductItemInfos = new ArrayList<>();

        for (OrderItem item : orderItems) {
            // 查询当前库存
            Inventory inventory = inventoryMapper.selectBuProductId(item.getProductId());
            if (inventory == null)
                throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);

            // 检查库存是否充足
            if (inventory.getAvailableStock() < item.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK,
                    "商品 " + item.getProductId() + " 库存不足，当前库存: " +
                        inventory.getAvailableStock() + ", 需要: " + item.getQuantity());            }

            // 使用乐观锁更新库存
            int updateCount = inventoryMapper.deductStock(
                item.getProductId(),
                item.getQuantity(),
                inventory.getVersion()
            );

            if (updateCount == 0)
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "库存更新失败，可能由于并发操作，请重试");

            // 记录库存变更历史
            InventoryHistory history = new InventoryHistory();
            history.setProductId(item.getProductId());
            history.setChangeType(InventoryHistory.ChangeType.DECREMENT.name());
            history.setChangeAmount(item.getQuantity());
            history.setBeforeStock(inventory.getAvailableStock());
            history.setAfterStock(inventory.getAvailableStock() - item.getQuantity());
            history.setReferenceId(orderId);
            history.setReason("订单扣减");
            history.setOperator("deductInventory");
            LocalDateTime now = LocalDateTime.now();
            history.setCreateTime(now);
            historyMapper.insert(history);

            // 返回信息
            InventoryDeductResponse.DeductItemInfo deductItemInfo = new InventoryDeductResponse.DeductItemInfo();
            deductItemInfo.setProductId(item.getProductId());

            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                deductItemInfo.setProductName(product.getProductName());
            }

            deductItemInfo.setQuantity(item.getQuantity());
            deductItemInfo.setRemainingStock(inventory.getAvailableStock() - item.getQuantity());
            deductItemInfos.add(deductItemInfo);
        }
        response.setItems(deductItemInfos);
        return response;
    }

    @Override
    public InventoryResponse getInventory(String productId) {
        Inventory inventory = inventoryMapper.selectBuProductId(productId);
        if (inventory == null)
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);

        Product product = productMapper.selectById(productId);

        InventoryResponse response = new InventoryResponse();
        response.setProductId(productId);
        response.setProductName(product.getProductName());
        response.setStock(inventory.getAvailableStock());
        response.setUnit(inventory.getUnit());
        response.setLastUpdated(inventory.getLastUpdated());
        return response;
    }

    @Override
    @Transactional
    public InventoryAdjustResponse adjustInventory(String productId, InventoryAdjustRequest request) {
        Inventory inventory = inventoryMapper.selectBuProductId(productId);
        if (inventory == null)
            throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);

        int oldStock = inventory.getAvailableStock();
        int newStock;

        if ("increase".equals(request.getAdjustType())) {
            newStock = oldStock + request.getAmount();
        }  else if ("decrease".equals(request.getAdjustType())) {
            if (oldStock < request.getAmount()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }
            newStock = oldStock - request.getAmount();
        } else {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "不支持的调整类型");
        }

        // 更新库存
        inventory.setAvailableStock(newStock);
        inventory.setTotalStock(newStock);
        inventory.setLastUpdated(LocalDateTime.now());
        inventoryMapper.updateByProductId(inventory); // 不用手动传入inventoryId，(该字段已经标记为TableId, mybatis-plus会自动处理)

        // 记录变更历史
        InventoryHistory history = new InventoryHistory();
        history.setProductId(productId);
        if ("increase".equals(request.getAdjustType())) {
            history.setChangeType(InventoryHistory.ChangeType.INCREMENT.name());
        } else if ("decrease".equals(request.getAdjustType())) {
            history.setChangeType(InventoryHistory.ChangeType.DECREMENT.name());
        }
        history.setChangeAmount(request.getAmount());
        history.setBeforeStock(oldStock);
        history.setAfterStock(newStock);
        history.setReason(request.getReason());
        history.setOperator("adjustInventory");
        LocalDateTime now = LocalDateTime.now();
        history.setCreateTime(now);
        historyMapper.insert(history);

        InventoryAdjustResponse response = new InventoryAdjustResponse();
        response.setProductId(productId);
        response.setOldStock(oldStock);
        response.setNewStock(newStock);
        response.setMessage("库存调整成功");
        return response;
    }

    @Override
    public DeadLetterRetryResponse retryDeadLetter(DeadLetterRetryRequest request) {
        DeadLetterMessage deadLetter = deadLetterMapper.selectById(request.getMessageId());
        if (deadLetter == null) {
            throw new BusinessException(ErrorCode.DEAD_LETTER_NOT_FOUND);
        }

        // 检查重试次数
        if (deadLetter.getRetryCount() >= request.getMaxRetry()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                "死信消息重试次数已达上限，无法重新投递");
        }

        try {
            // 重新发送信息
            rabbitTemplate.convertAndSend(
                deadLetter.getTopic(),
                deadLetter.getMessageKey(),
                deadLetter.getMessageBody()
            );

            // 更新重试次数
            deadLetter.setRetryCount(deadLetter.getRetryCount() + 1);
            deadLetter.setLastRetryTime(LocalDateTime.now());
            deadLetterMapper.updateById(deadLetter);



            DeadLetterRetryResponse response = new DeadLetterRetryResponse();
            response.setMessageId(request.getMessageId());
            response.setStatus("RESUBMITTED");
            response.setRetryCount(deadLetter.getRetryCount());
            response.setMessage("死信消息重新投递成功");
            return response;
        } catch (Exception e) {
            // 重试失败，更新错误信息
            deadLetter.setLastRetryTime(LocalDateTime.now());
            deadLetter.setRetryCount(deadLetter.getRetryCount() + 1);
            deadLetterMapper.updateById(deadLetter);

            throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                "死信消息重新投递失败: " + e.getMessage());
        }


    }


    }
