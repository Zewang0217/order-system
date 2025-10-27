package org.zewang.ordersystem.dto.fulfillment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateTrackingRequest {
    private String status;
    private String location;
    private String description;
    private LocalDateTime trackTime;
}
