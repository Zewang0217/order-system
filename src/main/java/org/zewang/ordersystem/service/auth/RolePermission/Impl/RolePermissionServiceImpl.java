package org.zewang.ordersystem.service.auth.RolePermission.Impl;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 17:02
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.entity.user.RolePermission;
import org.zewang.ordersystem.mapper.RolePermissionMapper;
import org.zewang.ordersystem.service.auth.RolePermission.RolePermissionService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        // 检查是否已存在该角色权限关系
        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId).eq("permission_id", permissionId);
        if (rolePermissionMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(400, "角色已拥有该权限");
        }

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionId(permissionId);
        rolePermissionMapper.insert(rolePermission);
        log.info("为角色 {} 分配权限 {}", roleId, permissionId);
    }

    @Override
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId).eq("permission_id", permissionId);
        rolePermissionMapper.delete(queryWrapper);
        log.info("移除角色 {} 的权限 {}", roleId, permissionId);
    }
}