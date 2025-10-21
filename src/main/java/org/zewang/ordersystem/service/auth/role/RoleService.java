package org.zewang.ordersystem.service.auth.role;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 16:57
 */

import org.zewang.ordersystem.entity.user.Role;
import java.util.List;

public interface RoleService {

    /**
     * 创建角色
     * @param role 角色信息
     * @return 创建后的角色
     */
    Role createRole(Role role);

    /**
     * 获取所有角色
     * @return 角色列表
     */
    List<Role> getAllRoles();

    /**
     * 根据ID获取角色
     * @param roleId 角色ID
     * @return 角色信息
     */
    Role getRoleById(Long roleId);

    /**
     * 更新角色
     * @param roleId 角色ID
     * @param role 更新的角色信息
     * @return 更新后的角色
     */
    Role updateRole(Long roleId, Role role);

    /**
     * 删除角色
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);
}
