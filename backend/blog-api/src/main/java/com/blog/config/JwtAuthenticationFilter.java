package com.blog.config;

import cn.hutool.core.util.StrUtil;
import com.blog.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 如果是静态资源请求，直接放行
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/images/") || requestURI.startsWith("/uploads/")) {
            chain.doFilter(request, response);
            return;
        }
        
        String authHeader = request.getHeader("Authorization");

        // 添加调试日志
        System.out.println("JWT Filter - Request URI: " + requestURI);
        System.out.println("JWT Filter - Auth Header: " + authHeader);

        if (StrUtil.isBlankOrUndefined(authHeader)) {
            System.out.println("JWT Filter - No Auth Header found");
            chain.doFilter(request, response);
            return;
        }

        // 检查是否以Bearer开头
        String jwt;
        if (authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            System.out.println("JWT Filter - Bearer token found, extracted token");
        } else {
            // 尝试直接使用token
            jwt = authHeader;
            System.out.println("JWT Filter - Using raw token");
        }

        if (StrUtil.isBlankOrUndefined(jwt)) {
            System.out.println("JWT Filter - Empty token after extraction");
            chain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtUtils.validateToken(jwt)) {
                System.out.println("JWT Filter - Invalid token");
                chain.doFilter(request, response);
                return;
            }

            String userId = String.valueOf(jwtUtils.getUserIdFromToken(jwt));
            System.out.println("JWT Filter - Valid token for user ID: " + userId);

            // 获取用户的权限等信息
            UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            System.out.println("JWT Filter - User details loaded: " + userDetails.getUsername());
            System.out.println("JWT Filter - User authorities: " + userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 设置用户登录状态
            SecurityContextHolder.getContext().setAuthentication(token);
            System.out.println("JWT Filter - Authentication set in SecurityContext");
        } catch (Exception e) {
            System.out.println("JWT Filter - Error processing token: " + e.getMessage());
            e.printStackTrace();
        }

        chain.doFilter(request, response);
    }
}