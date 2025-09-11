package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.EmailService;
import com.blog.service.UserService;
import com.blog.utils.JwtUtils;
import com.blog.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    // 验证码缓存，存储格式：email -> {code, timestamp}
    private final Map<String, Map<String, Object>> verificationCodeCache = new ConcurrentHashMap<>();
    
    // 验证码有效期（10分钟）
    private static final long CODE_EXPIRE_TIME = 10 * 60 * 1000;

    @Override
    public UserVo login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).or().eq("email", username);
        User user = this.getOne(queryWrapper);
        
        // 用户不存在
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 密码错误
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 用户被禁用
        if (user.getStatus() == 0) {
            throw new RuntimeException("账户被禁用");
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
        user.setRole(1); // 设置为普通用户角色
        
        // 设置默认头像
        if (user.getAvatar() == null || user.getAvatar().trim().isEmpty()) {
            user.setAvatar("https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png");
        }
        
        // 设置默认昵称
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        
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

    @Override
    public boolean sendForgotPasswordCode(String email) {
        try {
            // 检查邮箱是否存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", email);
            User user = this.getOne(queryWrapper);
            
            if (user == null) {
                throw new RuntimeException("该邮箱未注册，请检查邮箱地址或先注册账户");
            }
            
            if (user.getStatus() == 0) {
                throw new RuntimeException("您的账户已被禁用，请联系管理员");
            }
            
            // 生成6位数字验证码
            String code = generateVerificationCode();
            
            // 发送验证码邮件
            boolean emailSent = emailService.sendVerificationCode(email, code, "忘记密码");
            
            if (emailSent) {
                // 将验证码存储到缓存中
                Map<String, Object> codeInfo = new HashMap<>();
                codeInfo.put("code", code);
                codeInfo.put("timestamp", System.currentTimeMillis());
                verificationCodeCache.put(email, codeInfo);
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean verifyForgotPasswordCode(String email, String code) {
        try {
            Map<String, Object> codeInfo = verificationCodeCache.get(email);
            
            if (codeInfo == null) {
                throw new RuntimeException("验证码不存在，请重新获取验证码");
            }
            
            String storedCode = (String) codeInfo.get("code");
            Long timestamp = (Long) codeInfo.get("timestamp");
            
            // 检查验证码是否过期
            if (System.currentTimeMillis() - timestamp > CODE_EXPIRE_TIME) {
                verificationCodeCache.remove(email); // 清除过期验证码
                throw new RuntimeException("验证码已过期，请重新获取验证码");
            }
            
            // 验证验证码
            if (!code.equals(storedCode)) {
                throw new RuntimeException("验证码错误，请重新输入");
            }
            
            return true;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("验证码验证失败，请重试");
        }
    }

    @Override
    public boolean resetPasswordByCode(String email, String code, String newPassword) {
        try {
            // 先验证验证码
            verifyForgotPasswordCode(email, code);
            
            // 查找用户
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", email);
            User user = this.getOne(queryWrapper);
            
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            if (user.getStatus() == 0) {
                throw new RuntimeException("您的账户已被禁用，请联系管理员");
            }
            
            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdateTime(new Date());
            boolean updated = this.updateById(user);
            
            if (updated) {
                // 清除验证码缓存
                verificationCodeCache.remove(email);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 生成6位数字验证码
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}