package org.zewang.ordersystem.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.zewang.ordersystem.config.JwtProperties;
import org.zewang.ordersystem.entity.user.Permission;
import org.zewang.ordersystem.entity.user.Role;
import org.zewang.ordersystem.mapper.PermissionMapper;
import org.zewang.ordersystem.mapper.RoleMapper;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/10/20 10:51
 */

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    // 从配置中获取密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // 从token中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从token中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 提取claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 解析所有claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    // 检查token是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 生成token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // 如果是自定义UserDetails实现，则添加角色和权限信息
        if (userDetails instanceof UserDetailsImpl) {
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            Long userId = userDetailsImpl.getUserId();

            // 添加角色信息
            List<Role> roles = roleMapper.findRolesByUserId(userId);
            List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
            claims.put("roles", roleNames);

            // 添加权限信息
            List<Permission> permissions = permissionMapper.findPermissionsByUserId(userId);
            List<String> permissionStrings = permissions.stream()
                .map(p -> p.getResource() + ":" + p.getAction())
                .collect(Collectors.toList());
            claims.put("permissions", permissionStrings);
        }

        return createToken(claims, userDetails.getUsername());
    }

    // 重载generateToken方法，支持传入用户ID
    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();

        if (userId != null) {
            // 添加角色信息
            List<Role> roles = roleMapper.findRolesByUserId(userId);
            List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
            claims.put("roles", roleNames);

            // 添加权限信息
            List<Permission> permissions = permissionMapper.findPermissionsByUserId(userId);
            List<String> permissionStrings = permissions.stream()
                .map(p -> p.getResource() + ":" + p.getAction())
                .collect(Collectors.toList());
            claims.put("permissions", permissionStrings);
        }

        return createToken(claims, userDetails.getUsername());
    }

    // 创建token
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }



    // 验证token
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // 从请求头中提取token
    public String extractTokenFromHeader(String headerValue) {
        if (headerValue != null && headerValue.startsWith(jwtProperties.getTokenPrefix())) {
            return headerValue.substring(jwtProperties.getTokenPrefix().length()).trim();
        }
        return null;
    }
}