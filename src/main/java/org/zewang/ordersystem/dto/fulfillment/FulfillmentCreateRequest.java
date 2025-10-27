package org.zewang.ordersystem.dto.fulfillment;

import lombok.Data;

@Data
public class FulfillmentCreateRequest {
    private String orderId;
    private String carrier;
    private String trackingNumber;
    private String shippingAddress;
    private java.time.LocalDateTime estimatedDelivery;
}