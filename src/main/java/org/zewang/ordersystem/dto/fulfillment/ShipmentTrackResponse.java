package org.zewang.ordersystem.dto.fulfillment;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShipmentTrackResponse {
    private Long trackId;
    private String location;
    private String description;
    private LocalDateTime trackTime;
    private LocalDateTime createTime;
}
