package org.zewang.ordersystem.service.auth.permission;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 16:58
 */


import org.zewang.ordersystem.entity.user.Permission;
import java.util.List;

public interface PermissionService {

    /**
     * 创建权限
     * @param permission 权限信息
     * @return 创建后的权限
     */
    Permission createPermission(Permission permission);

    /**
     * 获取所有权限
     * @return 权限列表
     */
    List<Permission> getAllPermissions();

    /**
     * 根据ID获取权限
     * @param permissionId 权限ID
     * @return 权限信息
     */
    Permission getPermissionById(Long permissionId);

    /**
     * 更新权限
     * @param permissionId 权限ID
     * @param permission 更新的权限信息
     * @return 更新后的权限
     */
    Permission updatePermission(Long permissionId, Permission permission);

    /**
     * 删除权限
     * @param permissionId 权限ID
     */
    void deletePermission(Long permissionId);
}
