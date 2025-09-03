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
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/auth/**").permitAll() // 允许认证相关的API
                .antMatchers("/api/settings/public").permitAll() // 允许访问公共设置
                .antMatchers("/api/article/list").permitAll() // 允许访问文章列表
                .antMatchers("/api/article/{id}").permitAll() // 允许访问文章详情
                .antMatchers("/api/category/list").permitAll() // 允许访问分类列表
                .antMatchers("/api/tags").permitAll() // 允许访问标签列表
                .antMatchers("/api/statistics/**").permitAll() // 允许访问统计接口
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
