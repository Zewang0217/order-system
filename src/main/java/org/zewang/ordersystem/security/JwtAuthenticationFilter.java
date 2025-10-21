// 文件路径: src/main/java/org/zewang/ordersystem/security/JwtAuthenticationFilter.java
package org.zewang.ordersystem.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zewang.ordersystem.config.JwtProperties;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(jwtProperties.getHeader());

        String username = null;
        String jwt = null;

        // 检查Authorization头部是否包含Bearer token
        if (authorizationHeader != null && authorizationHeader.startsWith(jwtProperties.getTokenPrefix())) {
            jwt = jwtUtil.extractTokenFromHeader(authorizationHeader);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // Token解析异常
            }
        }

        // 如果token有效且Security上下文中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, username)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        log.debug("Authorization header: {}", authorizationHeader);
        log.debug("Extracted JWT: {}", jwt);
        log.debug("Extracted username: {}", username);

        chain.doFilter(request, response);
    }
}
