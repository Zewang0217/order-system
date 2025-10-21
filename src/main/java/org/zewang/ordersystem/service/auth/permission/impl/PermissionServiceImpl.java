package org.zewang.ordersystem.service.auth.permission.impl;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 16:59
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.entity.user.Permission;
import org.zewang.ordersystem.mapper.PermissionMapper;
import org.zewang.ordersystem.service.auth.permission.PermissionService;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public Permission createPermission(Permission permission) {
        permissionMapper.insert(permission);
        log.info("创建权限: {}", permission.getPermissionName());
        return permission;
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionMapper.selectList(null);
    }

    @Override
    public Permission getPermissionById(Long permissionId) {
        Permission permission = permissionMapper.selectById(permissionId);
        if (permission == null) {
            throw new BusinessException(404, "权限不存在");
        }
        return permission;
    }

    @Override
    public Permission updatePermission(Long permissionId, Permission permission) {
        Permission existingPermission = this.getPermissionById(permissionId);
        existingPermission.setPermissionName(permission.getPermissionName());
        existingPermission.setResource(permission.getResource());
        existingPermission.setAction(permission.getAction());
        permissionMapper.updateById(existingPermission);
        log.info("更新权限: {}", permissionId);
        return existingPermission;
    }

    @Override
    public void deletePermission(Long permissionId) {
        Permission permission = this.getPermissionById(permissionId);
        permissionMapper.deleteById(permissionId);
        log.info("删除权限: {}", permission.getPermissionName());
    }
}
