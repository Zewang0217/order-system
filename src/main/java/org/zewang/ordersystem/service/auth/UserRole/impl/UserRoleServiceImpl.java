package org.zewang.ordersystem.service.auth.UserRole.impl;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 17:00
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.entity.user.UserRole;
import org.zewang.ordersystem.mapper.UserRoleMapper;
import org.zewang.ordersystem.service.auth.UserRole.UserRoleService;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    private final UserRoleMapper userRoleMapper;

    @Override
    public void assignRoleToUser(Long userId, Long roleId) {
        // 检查是否已存在该用户角色关系
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("role_id", roleId);
        if (userRoleMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(400, "用户已拥有该角色");
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);
        log.info("为用户 {} 分配角色 {}", userId, roleId);
    }

    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("role_id", roleId);
        userRoleMapper.delete(queryWrapper);
        log.info("移除用户 {} 的角色 {}", userId, roleId);
    }
}
