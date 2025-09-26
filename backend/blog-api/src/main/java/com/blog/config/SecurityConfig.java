package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/uploads/**").permitAll() // 允许访问上传的文件
                .antMatchers("/api/upload/**").permitAll() // 允许文件上传接口
                .antMatchers("/upload/**").permitAll() // 允许简化的上传接口
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/auth/**").permitAll() // 允许认证相关的API
                .antMatchers("/api/about/**").permitAll() // 允许访问关于我页面
                .antMatchers("/api/admin/about/**").permitAll() // 允许访问关于我管理页面
                .antMatchers("/api/settings/public").permitAll() // 允许访问公共设置
                .antMatchers("/api/article/list").permitAll() // 允许访问文章列表
                .antMatchers("/api/article/{id}").permitAll() // 允许访问文章详情
                .antMatchers("/api/category/list").permitAll() // 允许访问分类列表
                .antMatchers("/api/tag/list").permitAll() // 允许访问标签列表
                .antMatchers("/api/photo/list").permitAll() // 允许访问照片列表
                .antMatchers("/api/photo/category/list").permitAll() // 允许访问照片分类列表
                .antMatchers("/api/comment/list").permitAll() // 允许访问评论列表
                .antMatchers("/api/comment/tree").permitAll() // 允许访问评论树
                .antMatchers("/api/comment/count/**").permitAll() // 允许访问评论数量
                .antMatchers("/api/friend-links").permitAll() // 允许访问友链列表
                .antMatchers("/api/friend-links/apply").permitAll() // 允许申请友链
                .antMatchers("/api/statistics/**").permitAll() // 允许访问统计接口
                .antMatchers("/api/ai/chat/**").permitAll() // 允许访问AI聊天功能（游客和用户都可以使用）
                .antMatchers("/api/ai/test-connection").permitAll() // 允许测试AI连接
                .antMatchers("/api/admin/**").hasRole("ADMIN") // 管理员接口需要ADMIN角色
                .anyRequest().authenticated()
            .and()
            .csrf().disable()
            .cors() // 启用CORS支持
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
