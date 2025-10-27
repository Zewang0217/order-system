package org.zewang.ordersystem.service.fulfillment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.zewang.ordersystem.dto.fulfillment.*;

public interface FulfillmentService {
    // 查询发货信息
    FulfillmentDetailResponse getFulfillmentById(String fulfillmentId);

    IPage<FulfillmentSummaryResponse> listFulfillments(Integer page, Integer size, String status);

    void updateTrackingInfo(String fulfillmentId, UpdateTrackingRequest request);

    void handleThirdPartyCallback(ThirdPartyLogisticsCallbackRequest request);

    RetryFulfillmentResponse retryFulfillment(String fulfillmentId);

    String createFulfillment(FulfillmentCreateRequest request);
}
