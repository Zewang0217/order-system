package org.zewang.ordersystem.enums;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:16
 */

public enum ErrorCode {
    SYSTEM_ERROR(500, "系统内部错误"),
    PARAM_ERROR(400, "参数错误"),
    ORDER_NOT_FOUND(1001, "订单不存在"),
    PRODUCT_NOT_FOUND(1002, "商品不存在"),
    INVENTORY_NOT_ENOUGH(1003, "库存不足"),
    PAYMENT_FAILED(1004, "支付失败"),
    USER_NOT_FOUND(1005, "用户不存在"),
    INCORRECT_USERNAME_OR_PASSWORD(401, "用户名或密码错误"),
    PAID_ORDER(409, "订单已支付"),
    INSUFFICIENT_STOCK(1006, "库存不足"),
    STOCK_UPDATE_FAILED(1007, "库存更新失败"),
    DEAD_LETTER_NOT_FOUND(1008, "死信消息不存在"),
    PAYMENT_CONFLICT(1009, "支付冲突"),
    PAYMENT_NOT_FOUND(400, "支付不存在"),
    INVALID_SIGNATURE(1010, "签名错误"),
    FULFILLMENT_NOT_FOUND(1011, "发货信息不存在"),
    FULFILLMENT_ALREADY_EXISTS(1012, "发货已经存在");



    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
