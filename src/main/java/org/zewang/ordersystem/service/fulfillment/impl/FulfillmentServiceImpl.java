package org.zewang.ordersystem.service.fulfillment.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.dto.fulfillment.FulfillmentCreateRequest;
import org.zewang.ordersystem.dto.fulfillment.FulfillmentDetailResponse;
import org.zewang.ordersystem.dto.fulfillment.FulfillmentSummaryResponse;
import org.zewang.ordersystem.dto.fulfillment.RetryFulfillmentResponse;
import org.zewang.ordersystem.dto.fulfillment.ShipmentTrackResponse;
import org.zewang.ordersystem.dto.fulfillment.ThirdPartyLogisticsCallbackRequest;
import org.zewang.ordersystem.dto.fulfillment.UpdateTrackingRequest;
import org.zewang.ordersystem.entity.fulfillment.Fulfillment;
import org.zewang.ordersystem.entity.fulfillment.ShipmentTrack;
import org.zewang.ordersystem.enums.ErrorCode;
import org.zewang.ordersystem.mapper.fulfillment.FulfillmentMapper;
import org.zewang.ordersystem.mapper.fulfillment.ShipmentTrackMapper;
import org.zewang.ordersystem.service.fulfillment.FulfillmentService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/22 21:04
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FulfillmentServiceImpl extends ServiceImpl<FulfillmentMapper, Fulfillment> implements
    FulfillmentService {

    private final FulfillmentMapper fulfillmentMapper;
    private final ShipmentTrackMapper shipmentTrackMapper;

    @Override
    public FulfillmentDetailResponse getFulfillmentById(String fulfillmentId) {
        Fulfillment fulfillment = fulfillmentMapper.selectById(fulfillmentId);
        if (fulfillment == null) {
            throw new BusinessException(ErrorCode.FULFILLMENT_NOT_FOUND);
        }

        // 查询物流轨迹
        QueryWrapper<ShipmentTrack> trackQueryWrapper = new QueryWrapper<>();
        trackQueryWrapper.eq("fulfillment_id", fulfillmentId);
        trackQueryWrapper.orderByAsc("track_time");
        List<ShipmentTrack> tracks = shipmentTrackMapper.selectList(trackQueryWrapper);

        // 组装响应
        FulfillmentDetailResponse response = new FulfillmentDetailResponse();
        response.setFulfillmentId(fulfillment.getFulfillmentId());
        response.setOrderId(fulfillment.getOrderId());
        response.setCarrier(fulfillment.getCarrier());
        response.setTrackingNumber(fulfillment.getTrackingNumber());
        response.setStatus(fulfillment.getStatus());
        response.setShippingAddress(fulfillment.getShippingAddress());
        response.setEstimatedDelivery(fulfillment.getEstimatedDelivery());
        response.setActualDelivery(fulfillment.getActualDelivery());
        response.setCreateTime(fulfillment.getCreateTime());
        response.setUpdateTime(fulfillment.getUpdateTime());

        List<ShipmentTrackResponse> trackResponses = tracks.stream().map(track -> {
            ShipmentTrackResponse trackResponse = new ShipmentTrackResponse();
            trackResponse.setTrackId(track.getTrackId());
            trackResponse.setLocation(track.getLocation());
            trackResponse.setDescription(track.getDescription());
            trackResponse.setTrackTime(track.getTrackTime());
            trackResponse.setCreateTime(track.getCreateTime());
            return trackResponse;
        }).collect(Collectors.toList());

        response.setTracks(trackResponses);
        return response;
    }

    @Override
    public IPage<FulfillmentSummaryResponse> listFulfillments(Integer page, Integer size,
        String status) {
        Page<Fulfillment> fulfillmentPage = new Page<>(page == null ? 1 : page,
            size == null ? 10 : size);
        QueryWrapper<Fulfillment> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");

        IPage<Fulfillment> result = fulfillmentMapper.selectPage(fulfillmentPage, queryWrapper);

        Page<FulfillmentSummaryResponse> responsePage = new Page<>();
        responsePage.setCurrent(result.getCurrent());
        responsePage.setSize(result.getSize());
        responsePage.setTotal(result.getTotal());
        responsePage.setPages(result.getPages());

        List<FulfillmentSummaryResponse> records = result
            .getRecords().stream().map(fulfillment -> {
                FulfillmentSummaryResponse response = new FulfillmentSummaryResponse();
                response.setFulfillmentId(fulfillment.getFulfillmentId());
                response.setOrderId(fulfillment.getOrderId());
                response.setCarrier(fulfillment.getCarrier());
                response.setTrackingNumber(fulfillment.getTrackingNumber());
                response.setStatus(fulfillment.getStatus());
                response.setCreateTime(fulfillment.getCreateTime());
                response.setUpdateTime(fulfillment.getUpdateTime());
                return response;
            }).collect(Collectors.toList());

        responsePage.setRecords(records);
        return responsePage;
    }

    @Override
    @Transactional
    public void updateTrackingInfo(String fulfillmentId, UpdateTrackingRequest request) {
        Fulfillment fulfillment = fulfillmentMapper.selectById(fulfillmentId);
        if (fulfillment == null) {
            throw new BusinessException(ErrorCode.FULFILLMENT_NOT_FOUND);
        }

        // 更新发货状态
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            fulfillment.setStatus(request.getStatus());

            // 如果状态是DELIVERED，则更新实际送达时间
            if ("DELIVERED".equals(request.getStatus())) {
                fulfillment.setActualDelivery(LocalDateTime.now());
            }

            fulfillment.setUpdateTime(LocalDateTime.now());
            fulfillmentMapper.updateById(fulfillment);
        }

        // 添加物流轨迹
        if (request.getLocation() != null && !request.getLocation().isEmpty()) {
            ShipmentTrack track = new ShipmentTrack();
            track.setFulfillmentId(fulfillmentId);
            track.setLocation(request.getLocation());
            track.setDescription(request.getDescription() != null ? request.getDescription() : "");
            track.setTrackTime(
                request.getTrackTime() != null ? request.getTrackTime() : LocalDateTime.now());
            shipmentTrackMapper.insert(track);
        }

        log.info("更新物流轨迹: {}, status: {}, location: {}",
            fulfillmentId, request.getStatus(), request.getLocation());

    }

    @Override
    @Transactional
    public void handleThirdPartyCallback(ThirdPartyLogisticsCallbackRequest request) {
        // 验证发货单是否存在
        QueryWrapper<Fulfillment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fulfillment_id", request.getFulfillmentId());
        Fulfillment fulfillment = fulfillmentMapper.selectOne(queryWrapper);

        if (fulfillment == null) {
            throw new BusinessException(ErrorCode.FULFILLMENT_NOT_FOUND);
        }

        // 更新物流单号 （如果有的话）
        if (request.getTrackingNumber() != null && !request.getTrackingNumber().isEmpty()) {
            fulfillment.setTrackingNumber(request.getTrackingNumber());
        }

        // 更新状态
        if (request.getEvent() != null && !request.getEvent().isEmpty()) {
            fulfillment.setStatus(request.getEvent());

            // 如果状态时DELIVERED，则更新实际送达时间
            if ("DELIVERED".equals(request.getEvent())) {
                fulfillment.setActualDelivery(request.getTimestamp());
            }

            fulfillment.setUpdateTime(LocalDateTime.now());
            fulfillmentMapper.updateById(fulfillment);
        }

        // 添加物流轨迹
        ShipmentTrack track = new ShipmentTrack();
        track.setFulfillmentId(request.getFulfillmentId());
        track.setLocation(request.getLocation() != null ? request.getLocation() : "");
        track.setDescription(request.getDescription() != null ? request.getDescription() : "");
        track.setTrackTime(request.getTimestamp() != null ? request.getTimestamp() : LocalDateTime.now());
        shipmentTrackMapper.insert(track);

        log.info("处理第三方支付回调， 发货：{}， 事件：{}", request.getFulfillmentId(), request.getEvent());

    }

    @Override
    @Transactional
    public RetryFulfillmentResponse retryFulfillment(String fulfillmentId) {
        Fulfillment fulfillment = fulfillmentMapper.selectById(fulfillmentId);
        if (fulfillment == null) {
            throw new BusinessException(ErrorCode.FULFILLMENT_NOT_FOUND);
        }

        RetryFulfillmentResponse response = new RetryFulfillmentResponse();

        try {
            // 这里可以添加具体的重试逻辑
            // 例如重新调用物流服务商API等

            // 增加重试次数
            fulfillment.setRetryCount(fulfillment.getRetryCount());
            fulfillment.setLastRetryTime(LocalDateTime.now());
            fulfillmentMapper.updateById(fulfillment);

            response.setSuccess(true);
            response.setMessage("Fulfillment retry initiated successfully");
            log.info("Retried fulfillment: {}", fulfillmentId);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Failed to retry fulfillment: " + e.getMessage());
            log.error("Failed to retry fulfillment: {}", fulfillmentId, e);
        }

        return response;
    }

    @Override
    @Transactional
    public String createFulfillment(FulfillmentCreateRequest request) {
        // 检查订单是否已经有关联的发货单
        QueryWrapper<Fulfillment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", request.getOrderId());
        if (fulfillmentMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.FULFILLMENT_ALREADY_EXISTS);
        }

        // 创建发货单
        Fulfillment fulfillment = new Fulfillment();
        String fulfillmentId = "FD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        fulfillment.setFulfillmentId(fulfillmentId);
        fulfillment.setOrderId(request.getOrderId());
        fulfillment.setCarrier(request.getCarrier());
        fulfillment.setTrackingNumber(request.getTrackingNumber());
        fulfillment.setShippingAddress(request.getShippingAddress());
        fulfillment.setEstimatedDelivery(request.getEstimatedDelivery());
        fulfillment.setStatus("PENDING"); // 初始状态为待发货

        fulfillmentMapper.insert(fulfillment);

        log.info("Created fulfillment: {} for order: {}", fulfillmentId, request.getOrderId());
        return fulfillmentId;
    }
}


