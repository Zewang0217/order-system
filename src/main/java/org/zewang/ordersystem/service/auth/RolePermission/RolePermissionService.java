package org.zewang.ordersystem.service.auth.RolePermission;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 17:01
 */

public interface RolePermissionService {

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     */
    void assignPermissionToRole(Long roleId, Long permissionId);

    /**
     * 移除角色的权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     */
    void removePermissionFromRole(Long roleId, Long permissionId);
}