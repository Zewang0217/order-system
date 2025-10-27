package org.zewang.ordersystem.dto.fulfillment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ThirdPartyLogisticsCallbackRequest {
    private String fulfillmentId;
    private String trackingNumber;
    private String event;
    private String location;
    private String description;
    private LocalDateTime timestamp;
}
