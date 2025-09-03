package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.utils.Result;
import com.blog.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result getCurrentUserInfo(@RequestHeader("Authorization") String authHeader) {
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

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 清除密码信息
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            boolean success = userService.updateUserInfo(user);
            if (success) {
                return Result.success("用户信息更新成功");
            } else {
                return Result.error("用户信息更新失败");
            }
        } catch (Exception e) {
            return Result.error("用户信息更新失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询用户列表（管理员功能）
     */
    @GetMapping("/list")
    public Result getUserList(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) String nickname,
                             @RequestParam(required = false) Integer role,
                             @RequestParam(required = false) Integer status) {
        try {
            // 构建搜索关键词
            String keyword = "";
            if (username != null && !username.trim().isEmpty()) {
                keyword = username;
            } else if (nickname != null && !nickname.trim().isEmpty()) {
                keyword = nickname;
            }
            
            return Result.success(userService.getUserList(page, size, keyword));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户状态（管理员权限）
     */
    @PutMapping("/{id}/status")
    public Result updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = userService.updateUserStatus(id, status);
            if (success) {
                return Result.success("用户状态更新成功");
            } else {
                return Result.error("用户状态更新失败");
            }
        } catch (Exception e) {
            return Result.error("用户状态更新失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户角色（管理员权限）
     */
    @PutMapping("/{id}/role")
    public Result updateUserRole(@PathVariable Long id, @RequestParam Integer role) {
        try {
            boolean success = userService.updateUserRole(id, role);
            if (success) {
                return Result.success("用户角色更新成功");
            } else {
                return Result.error("用户角色更新失败");
            }
        } catch (Exception e) {
            return Result.error("用户角色更新失败：" + e.getMessage());
        }
    }

    /**
     * 重置用户密码（管理员权限）
     */
    @PutMapping("/{id}/reset-password")
    public Result resetPassword(@PathVariable Long id) {
        try {
            boolean success = userService.resetPassword(id);
            if (success) {
                return Result.success("密码重置成功，新密码为：123456");
            } else {
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            return Result.error("密码重置失败：" + e.getMessage());
        }
    }
}