package org.zewang.ordersystem.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.entity.user.Role;
import org.zewang.ordersystem.service.auth.role.RoleService;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 角色管理控制器
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 16:53
 */
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ApiResult<Role> createRole(@RequestBody Role role) {
        Role createdRole = roleService.createRole(role);
        return ApiResult.success(200, createdRole);
    }

    @GetMapping
    public ApiResult<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ApiResult.success(200, roles);
    }

    @PutMapping("/{roleId}")
    public ApiResult<Role> updateRole(@PathVariable Long roleId, @RequestBody Role role) {
        Role updatedRole = roleService.updateRole(roleId, role);
        return ApiResult.success(200, updatedRole);
    }

    @DeleteMapping("/{roleId}")
    public ApiResult<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ApiResult.success(200, null);
    }

}
