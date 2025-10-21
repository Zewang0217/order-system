// 文件路径: src/main/java/org/zewang/ordersystem/security/UserDetailsServiceImpl.java
package org.zewang.ordersystem.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zewang.ordersystem.entity.user.Permission;
import org.zewang.ordersystem.entity.user.Role;
import org.zewang.ordersystem.mapper.PermissionMapper;
import org.zewang.ordersystem.mapper.RoleMapper;
import org.zewang.ordersystem.mapper.UserMapper;

// 作用：根据用户名从数据库加载用户信息，并转为spring security可识别的userDetails对象。
// 在认证过程中提供用户凭证和权限信息

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        org.zewang.ordersystem.entity.user.User user =
            userMapper.selectOne(new QueryWrapper<org.zewang.ordersystem.entity.user.User>()
                .eq("username", username));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 获取用户角色
        List<Role> roles = roleMapper.findRolesByUserId(user.getUserId());
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        }

        // 获取用户权限
        List<Permission> permissions = permissionMapper.findPermissionsByUserId(user.getUserId());
        for (Permission permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(
                permission.getResource() + ":" + permission.getAction()));
        }

        return new UserDetailsImpl(
            user.getUserId(),
            user.getUsername(),
            user.getPassword(),
            authorities
        );
    }
}
