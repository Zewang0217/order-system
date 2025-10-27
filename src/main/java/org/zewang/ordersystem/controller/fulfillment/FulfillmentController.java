package org.zewang.ordersystem.controller.fulfillment;


import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.dto.fulfillment.FulfillmentDetailResponse;
import org.zewang.ordersystem.dto.fulfillment.FulfillmentSummaryResponse;
import org.zewang.ordersystem.dto.fulfillment.RetryFulfillmentResponse;
import org.zewang.ordersystem.dto.fulfillment.ThirdPartyLogisticsCallbackRequest;
import org.zewang.ordersystem.dto.fulfillment.UpdateTrackingRequest;
import org.zewang.ordersystem.service.fulfillment.FulfillmentService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/23 18:22
 */

@RestController
@RequestMapping("/api/fulfillment")
@RequiredArgsConstructor
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;

    // 创建发货详情
    @GetMapping("/{fulfillmentId}")
    public ApiResult<FulfillmentDetailResponse> getFulfillmentById(
        @PathVariable String fulfillmentId) {
        FulfillmentDetailResponse response = fulfillmentService.getFulfillmentById(fulfillmentId);
        return ApiResult.success(response);
    }

    // 分页查询发货列表
    @GetMapping
    public ApiResult<IPage<FulfillmentSummaryResponse>> listFulfillments(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "20") Integer size,
        @RequestParam(required = false) String status
    ) {
        IPage<FulfillmentSummaryResponse> response = fulfillmentService.listFulfillments(page, size,
            status);
        return ApiResult.success(response);
    }

    // 更新物流信息
    @PutMapping("/{fulfillmentId}/update-tracking")
    public ApiResult<String> updateTrackingInfo(@PathVariable String fulfillmentId,
        @RequestBody UpdateTrackingRequest request) {
        fulfillmentService.updateTrackingInfo(fulfillmentId, request);
        return ApiResult.success("Tracking info updated successfully");
    }

    // 模拟第三方物流回调
    @PostMapping("/callback")
    public ApiResult<String> handleThirdPartyCallback(@RequestBody ThirdPartyLogisticsCallbackRequest request) {
        fulfillmentService.handleThirdPartyCallback(request);
        return ApiResult.success("Callback handled successfully");
    }

    // 重新发货
    @PostMapping("/{fulfillmentId}/retry")
    public ApiResult<RetryFulfillmentResponse> retryFulfillment(@PathVariable String fulfillmentId) {
        RetryFulfillmentResponse response = fulfillmentService.retryFulfillment(fulfillmentId);
        return ApiResult.success(response);
    }

}
