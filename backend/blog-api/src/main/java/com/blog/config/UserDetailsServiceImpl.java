package com.blog.config;

import com.blog.mapper.UserMapper;
import com.blog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详情服务实现类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String usernameOrId) throws UsernameNotFoundException {
        User user;
        try {
            // 判断是用户名还是用户ID
            if (usernameOrId.matches("\\d+")) {
                // 如果是数字，认为是极ID
                user = userMapper.selectById(Long.parseLong(usernameOrId));
            } else {
                // 否则认为是用户名，使用 MyBatis Plus 的查询条件构造器
                user = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User>()
                        .eq("username", usernameOrId));
            }
            
            if (user == null) {
                throw new UsernameNotFoundException("用户不存在");
            }
        } catch (Exception e) {
            throw new UsernameNotFoundException("用户查询失败: " + e.getMessage());
        }

        // 构建用户权限
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() == 0) {
            // 管理员权限
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            // 普通用户权限
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // 返回UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(), // 使用用户ID作为用户名，以便在SecurityUtils中可以正确提取
                user.getPassword(),
                user.getStatus() == 1, // 账户是否启用
                true, // 账户是否未过期
                true, // 凭证是否未过期
                true, // 账户是否未锁定
                authorities
        );
    }
}