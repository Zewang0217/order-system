package org.zewang.ordersystem.service.auth.UserRole;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 17:00
 */


public interface UserRoleService {

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void assignRoleToUser(Long userId, Long roleId);

    /**
     * 移除用户的角色
     * @param userId 用户ID
     * @param roleId 角色ID
     */
    void removeRoleFromUser(Long userId, Long roleId);
}
