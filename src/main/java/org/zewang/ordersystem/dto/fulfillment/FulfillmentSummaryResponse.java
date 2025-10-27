package org.zewang.ordersystem.dto.fulfillment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FulfillmentSummaryResponse {
    private String fulfillmentId;
    private String orderId;
    private String carrier;
    private String trackingNumber;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
