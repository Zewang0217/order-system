package org.zewang.ordersystem.dto.fulfillment;

import lombok.Data;

@Data
public class RetryFulfillmentResponse {
    private Boolean success;
    private String message;
}
