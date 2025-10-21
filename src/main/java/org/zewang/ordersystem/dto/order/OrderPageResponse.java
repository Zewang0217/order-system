package org.zewang.ordersystem.dto.order;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 18:12
 */

import lombok.Data;
import java.util.List;

@Data
public class OrderPageResponse {
    private Integer page;
    private Integer size;
    private Long total;
    private List<OrderRecord> records;

    @Data
    public static class OrderRecord {
        private String orderId;
        private String status;
        private String createdAt;
    }
}
