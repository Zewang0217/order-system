package org.zewang.ordersystem.controller;


/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 17:05
 */

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.zewang.ordersystem.common.api.ApiResult;
import org.zewang.ordersystem.service.auth.UserRole.UserRoleService;

@RestController
@RequestMapping("/api/admin/user-roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    public ApiResult<Void> assignRoleToUser(@RequestParam Long userId, @RequestParam Long roleId) {
        userRoleService.assignRoleToUser(userId, roleId);
        return ApiResult.success(200, null);
    }

    @DeleteMapping
    public ApiResult<Void> removeRoleFromUser(@RequestParam Long userId, @RequestParam Long roleId) {
        userRoleService.removeRoleFromUser(userId, roleId);
        return ApiResult.success(200, null);
    }
}
