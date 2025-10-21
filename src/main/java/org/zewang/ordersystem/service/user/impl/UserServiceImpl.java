package org.zewang.ordersystem.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.dto.user.UserInfoResponse;
import org.zewang.ordersystem.dto.user.UserLoginRequest;
import org.zewang.ordersystem.dto.user.UserLoginResponse;
import org.zewang.ordersystem.dto.user.UserRegisterRequest;
import org.zewang.ordersystem.dto.user.UserUpdateRequest;
import org.zewang.ordersystem.entity.user.User;
import org.zewang.ordersystem.enums.ErrorCode;
import org.zewang.ordersystem.mapper.UserMapper;
import org.zewang.ordersystem.security.JwtUtil;
import org.zewang.ordersystem.service.user.UserService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:30
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements  UserService {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserInfoResponse register(UserRegisterRequest request) {
        // 检查用户名是否存在
        if (this.findByUsername(request.getUsername()) != null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "用户名已经存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("USER");
        user.setStatus("ACTIVE");

        this.save(user);

        // 返回用户信息
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreateTime(user.getCreateTime());

        return response;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = this.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.INCORRECT_USERNAME_OR_PASSWORD);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INCORRECT_USERNAME_OR_PASSWORD);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // 生成token
        String token = jwtUtil.generateToken(userDetails);

        // 构造返回结果
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);

        UserLoginResponse.UserInfo userInfo = new UserLoginResponse.UserInfo();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole());
        response.setUserInfo(userInfo);

        return response;
    }

    // 根据id查找用户
    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setCreateTime(user.getCreateTime());

        return response;
    }

    // 更新地址
    @Override
    public UserInfoResponse updateAddress(Long userId, String address) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setAddress(address);
        this.updateById(user);

        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getUserId());
        response.setAddress(user.getAddress());

        return response;
    }

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    @Override
    public UserInfoResponse updateUserInfo(Long userId, UserUpdateRequest request) {
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        this.updateById(user);

        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCreateTime(user.getCreateTime());

        return response;
    }
}
