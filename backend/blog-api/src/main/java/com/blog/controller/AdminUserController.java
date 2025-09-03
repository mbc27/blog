package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户管理控制器
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/users")
    public Result getUserList(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) String nickname,
                             @RequestParam(required = false) Integer role,
                             @RequestParam(required = false) Integer status) {
        System.out.println("AdminUserController - 接收到获取用户列表请求: page=" + page + " size=" + size + " username=" + username + " nickname=" + nickname + " role=" + role + " status=" + status);
        
        try {
            // 构建搜索关键词
            String keyword = "";
            if (username != null && !username.trim().isEmpty()) {
                keyword = username;
            } else if (nickname != null && !nickname.trim().isEmpty()) {
                keyword = nickname;
            }
            
            IPage<User> result = userService.getAllUsers(page, size, username, nickname, role, status);
            
            // 清除密码信息
            result.getRecords().forEach(user -> user.setPassword(null));
            
            System.out.println("AdminUserController - 查询完成，返回 " + result.getRecords().size() + " 条记录，总数: " + result.getTotal());
            return Result.success("获取用户列表成功", result);
        } catch (Exception e) {
            System.err.println("AdminUserController - 查询失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/users/{id}")
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
     * 创建用户
     */
    @PostMapping("/users")
    public Result createUser(@RequestBody User user) {
        try {
            boolean success = userService.register(user);
            if (success) {
                return Result.success("用户创建成功");
            } else {
                return Result.error("用户创建失败，用户名或邮箱已存在");
            }
        } catch (Exception e) {
            return Result.error("用户创建失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/users/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            boolean success = userService.updateUserInfo(user);
            if (success) {
                return Result.success("用户更新成功");
            } else {
                return Result.error("用户更新失败");
            }
        } catch (Exception e) {
            return Result.error("用户更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{id}")
    public Result deleteUser(@PathVariable Long id) {
        try {
            boolean success = userService.deleteUser(id);
            if (success) {
                return Result.success("用户删除成功");
            } else {
                return Result.error("用户删除失败");
            }
        } catch (Exception e) {
            return Result.error("用户删除失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{id}/status")
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
     * 更新用户角色
     */
    @PutMapping("/users/{id}/role")
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
     * 重置用户密码
     */
    @PutMapping("/users/{id}/reset-password")
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