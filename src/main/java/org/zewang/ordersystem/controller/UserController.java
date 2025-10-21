package org.zewang.ordersystem.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.dto.user.UpdateAddressRequest;
import org.zewang.ordersystem.dto.user.UserInfoResponse;
import org.zewang.ordersystem.dto.user.UserLoginRequest;
import org.zewang.ordersystem.dto.user.UserLoginResponse;
import org.zewang.ordersystem.dto.user.UserRegisterRequest;
import org.zewang.ordersystem.dto.user.UserUpdateRequest;
import org.zewang.ordersystem.service.user.UserService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:44
 */

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResult<UserInfoResponse> register(@RequestBody UserRegisterRequest request) {
        UserInfoResponse response = userService.register(request);
        return ApiResult.success(200, response);
    }

    @PostMapping("/login")
    public ApiResult<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ApiResult.success(200, response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ApiResult<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
        UserInfoResponse response = userService.getUserInfo(userId);
        return ApiResult.success(200, response);
    }

    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    @PutMapping("/{userId}/address")
    public ApiResult<UserInfoResponse> updateAddress(
        @PathVariable Long userId,
        @RequestBody UpdateAddressRequest request) {
        UserInfoResponse response = userService.updateAddress(userId, request.getAddress());
        return ApiResult.success(200, response);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    public ApiResult<UserInfoResponse> updateUserInfo(
        @PathVariable Long userId,
        @RequestBody UserUpdateRequest request) {
        UserInfoResponse response = userService.updateUserInfo(userId, request);
        return ApiResult.success(200, response);
    }

}

