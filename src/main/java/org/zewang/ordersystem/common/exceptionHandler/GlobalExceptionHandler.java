package org.zewang.ordersystem.common.exceptionHandler;


import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.common.exception.MessageProcessingException;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:18
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ApiResult<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数验证异常: {}", e.getMessage(), e);
        return ApiResult.error(400, "参数错误: " + e.getMessage());
    }

    /**
     * 处理认证异常（401）
     */
    @ExceptionHandler({org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class,
        org.springframework.security.authentication.BadCredentialsException.class})
    public ApiResult<Void> handleAuthenticationException(Exception e) {
        log.error("认证异常: {}", e.getMessage());
        return ApiResult.error(401, "用户名或密码错误");
    }

    /**
     * 处理权限异常（403）
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ApiResult<Void> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        log.error("权限异常: {}", e.getMessage());
        return ApiResult.error(403, "无权访问");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ApiResult.error(500, "系统内部错误");
    }

    @ExceptionHandler(MessageProcessingException.class)
    public ApiResult<Void> handleMessageProcessingException(MessageProcessingException e) {
        log.error("消息处理异常 - ID: {}, Topic: {}", e.getMessageId(), e.getTopic(), e);
        return ApiResult.error(500, "消息处理失败: " + e.getMessage());
    }
}
