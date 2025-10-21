package org.zewang.ordersystem.common.api;


import lombok.Data;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:10
 */

@Data
public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResult<T> success(int code, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.code = code;
        result.message = "success";
        result.data = data;
        return result;
    }


    public static <T> ApiResult<T> success(T data) {
        return success(200, data);
    }


    public static <T> ApiResult<T> error(int code, String message) {
        ApiResult<T> result = new ApiResult<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public static <T> ApiResult<T> error(String message) {
        return error(500, message);
    }


}
