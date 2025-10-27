package org.zewang.ordersystem.dto.fulfillment;

import lombok.Data;
import org.zewang.ordersystem.dto.fulfillment.ShipmentTrackResponse;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FulfillmentDetailResponse {
    private String fulfillmentId;
    private String orderId;
    private String carrier;
    private String trackingNumber;
    private String status;
    private String shippingAddress;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualDelivery;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<ShipmentTrackResponse> tracks;
}
