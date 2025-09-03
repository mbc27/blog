package com.blog.controller;

import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 临时控制器，用于修复管理员用户角色
 * 注意：此控制器仅用于修复问题，应在修复后删除
 */
@RestController
@RequestMapping("/api/admin-fix")
public class AdminFixController {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 修复管理员用户角色
     * @return 修复结果
     */
    @GetMapping("/fix-admin-role")
    public Result fixAdminRole() {
        try {
            // 确保admin用户的角色值为0（管理员）
            int updateCount = jdbcTemplate.update("UPDATE tb_user SET role = 0 WHERE username = 'admin'");
            
            // 检查是否存在superadmin用户
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_user WHERE username = 'superadmin'", Integer.class);
            
            // 如果不存在，则添加一个新的管理员用户
            if (count != null && count == 0) {
                jdbcTemplate.update(
                    "INSERT INTO tb_user (username, password, nickname, avatar, email, phone, status, role) " +
                    "VALUES ('superadmin', '$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm', '超级管理员', " +
                    "'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', " +
                    "'superadmin@example.com', '13800138001', 1, 0)"
                );
            }
            
            return Result.success("管理员用户角色修复成功，已更新 " + updateCount + " 条记录");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修复失败：" + e.getMessage());
        }
    }
}