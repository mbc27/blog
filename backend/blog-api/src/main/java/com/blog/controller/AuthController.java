package com.blog.controller;

import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.utils.Result;
import com.blog.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }

        try {
            UserVo userVo = userService.login(username, password);
            if (userVo != null) {
                return Result.success("登录成功", userVo);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return Result.error("用户名、密码和邮箱不能为空");
        }

        try {
            boolean success = userService.register(user);
            if (success) {
                return Result.success("注册成功");
            } else {
                return Result.error("注册失败，用户名或邮箱已存在");
            }
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/user/info")
    public Result getUserInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.error("未提供有效的认证令牌");
        }
        
        String token = authHeader.substring(7);
        try {
            UserVo userVo = userService.getUserInfoByToken(token);
            if (userVo != null) {
                return Result.success("获取用户信息成功", userVo);
            } else {
                return Result.error("用户信息获取失败");
            }
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }
}