package org.zewang.ordersystem.security;


import java.util.Collection;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 存储用户的认证信息（用户名、密码等）
 *   包含用户的权限和角色信息
 *   提供账户状态信息（过期？锁定？）
 *   可以扩展包含额外用户信息（如用户ID）
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 11:18
 */

@Data
public class UserDetailsImpl implements UserDetails {
    private Long userId;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    public UserDetailsImpl(Long userId, String username, String password,
        Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
