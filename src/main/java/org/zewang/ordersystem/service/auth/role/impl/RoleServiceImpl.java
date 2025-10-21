package org.zewang.ordersystem.service.auth.role.impl;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 16:58
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zewang.ordersystem.common.exception.BusinessException;
import org.zewang.ordersystem.entity.user.Role;
import org.zewang.ordersystem.mapper.RoleMapper;
import org.zewang.ordersystem.service.auth.role.RoleService;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public Role createRole(Role role) {
        // 检查角色名是否已存在
        Role existingRole = roleMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Role>()
                .eq("role_name", role.getRoleName())
        );

        if (existingRole != null) {
            throw new BusinessException(400, "角色名已存在");
        }

        roleMapper.insert(role);
        log.info("创建角色: {}", role.getRoleName());
        return role;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.selectList(null);
    }

    @Override
    public Role getRoleById(Long roleId) {
        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(404, "角色不存在");
        }
        return role;
    }

    @Override
    public Role updateRole(Long roleId, Role role) {
        Role existingRole = this.getRoleById(roleId);
        existingRole.setRoleName(role.getRoleName());
        existingRole.setDescription(role.getDescription());
        roleMapper.updateById(existingRole);
        log.info("更新角色: {}", roleId);
        return existingRole;
    }

    @Override
    public void deleteRole(Long roleId) {
        Role role = this.getRoleById(roleId);
        roleMapper.deleteById(roleId);
        log.info("删除角色: {}", role.getRoleName());
    }
}