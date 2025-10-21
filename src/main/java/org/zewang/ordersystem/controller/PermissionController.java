package org.zewang.ordersystem.controller;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 17:04
 */

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.entity.user.Permission;
import org.zewang.ordersystem.service.auth.permission.PermissionService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ApiResult<Permission> createPermission(@RequestBody Permission permission) {
        Permission createdPermission = permissionService.createPermission(permission);
        return ApiResult.success(200, createdPermission);
    }

    @GetMapping
    public ApiResult<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ApiResult.success(200, permissions);
    }

    @PutMapping("/{permissionId}")
    public ApiResult<Permission> updatePermission(@PathVariable Long permissionId, @RequestBody Permission permission) {
        Permission updatedPermission = permissionService.updatePermission(permissionId, permission);
        return ApiResult.success(200, updatedPermission);
    }

    @DeleteMapping("/{permissionId}")
    public ApiResult<Void> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return ApiResult.success(200, null);
    }
}
