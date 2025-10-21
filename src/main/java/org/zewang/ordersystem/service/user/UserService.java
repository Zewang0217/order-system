package org.zewang.ordersystem.service.user;


import org.zewang.ordersystem.dto.user.UserInfoResponse;
import org.zewang.ordersystem.dto.user.UserLoginRequest;
import org.zewang.ordersystem.dto.user.UserLoginResponse;
import org.zewang.ordersystem.dto.user.UserRegisterRequest;
import org.zewang.ordersystem.dto.user.UserUpdateRequest;
import org.zewang.ordersystem.entity.user.User;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:26
 */

public interface UserService {

    // 用户注册
    UserInfoResponse register(UserRegisterRequest userRegisterRequest);

    // 用户登录
    UserLoginResponse login(UserLoginRequest request);

    // 根据用户ID获取用户信息
    UserInfoResponse getUserInfo(Long userId);

    // 更新用户地址
    UserInfoResponse updateAddress(Long userId, String address);

    // 根据用户名查找用户
    User findByUsername(String username);

    // 更新用户信息
    UserInfoResponse updateUserInfo(Long userId, UserUpdateRequest request);

}
