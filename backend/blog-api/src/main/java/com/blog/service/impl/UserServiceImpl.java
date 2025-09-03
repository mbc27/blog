package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import com.blog.utils.JwtUtils;
import com.blog.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserVo login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).or().eq("email", username);
        User user = this.getOne(queryWrapper);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            if (user.getStatus() == 0) {
                return null; // 用户被禁用
            }
            
            UserVo userVo = new UserVo();
            userVo.setId(user.getId());
            userVo.setUsername(user.getUsername());
            userVo.setNickname(user.getNickname());
            userVo.setEmail(user.getEmail());
            userVo.setAvatar(user.getAvatar());
            userVo.setRole(user.getRole());
            userVo.setStatus(user.getStatus());
            
            // 生成token
            String token = jwtUtils.generateToken(user.getId());
            userVo.setToken(token);
            
            return userVo;
        }
        return null;
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        if (this.getOne(queryWrapper) != null) {
            return false;
        }
        
        // 检查邮箱是否已存在
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", user.getEmail());
        if (this.getOne(queryWrapper) != null) {
            return false;
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setRole(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        
        return this.save(user);
    }

    @Override
    public IPage<User> getUserList(int page, int size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like("username", keyword)
                    .or().like("nickname", keyword)
                    .or().like("email", keyword);
        }
        
        queryWrapper.orderByAsc("id");
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public IPage<User> getAllUsers(int page, int size, String username, String nickname, Integer role, Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (nickname != null && !nickname.trim().isEmpty()) {
            queryWrapper.like("nickname", nickname);
        }
        if (role != null) {
            queryWrapper.eq("role", role);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        // 按ID升序排序
        queryWrapper.orderByAsc("id");
        
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public boolean updateUserInfo(User user) {
        user.setUpdateTime(new Date());
        return this.updateById(user);
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        user.setUpdateTime(new Date());
        return this.updateById(user);
    }

    @Override
    public boolean deleteUser(Long userId) {
        return this.removeById(userId);
    }

    @Override
    public boolean updateUserRole(Long userId, Integer role) {
        User user = new User();
        user.setId(userId);
        user.setRole(role);
        user.setUpdateTime(new Date());
        return this.updateById(user);
    }

    @Override
    public boolean resetPassword(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            String defaultPassword = "123456";
            String encodedPassword = passwordEncoder.encode(defaultPassword);
            user.setPassword(encodedPassword);
            user.setUpdateTime(new Date());
            return this.updateById(user);
        }
        return false;
    }

    @Override
    public UserVo getUserInfoByToken(String token) {
        try {
            // 从token中解析用户ID
            Long userId = jwtUtils.getUserIdFromToken(token);
            if (userId == null) {
                return null;
            }
            
            // 根据用户ID查询用户信息
            User user = this.getById(userId);
            if (user == null || user.getStatus() == 0) {
                return null; // 用户不存在或被禁用
            }
            
            // 构建返回的用户信息
            UserVo userVo = new UserVo();
            userVo.setId(user.getId());
            userVo.setUsername(user.getUsername());
            userVo.setNickname(user.getNickname());
            userVo.setEmail(user.getEmail());
            userVo.setAvatar(user.getAvatar());
            userVo.setRole(user.getRole());
            userVo.setStatus(user.getStatus());
            userVo.setToken(token); // 返回原token
            
            return userVo;
        } catch (Exception e) {
            return null;
        }
    }
}