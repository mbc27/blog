package com.blog.controller;

import com.blog.entity.SystemSettings;
import com.blog.service.SystemSettingsService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统设置管理控制器
 */
@RestController
@RequestMapping("/api/admin/settings")
@CrossOrigin
public class AdminSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    /**
     * 获取所有系统设置
     */
    @GetMapping
    public Result getSettings() {
        try {
            Map<String, String> settings = systemSettingsService.getAllSettings();
            return Result.success("获取系统设置成功", settings);
        } catch (Exception e) {
            return Result.error("获取系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 更新系统设置
     */
    @PutMapping
    public Result updateSettings(@RequestBody Map<String, String> settings) {
        try {
            systemSettingsService.updateSettings(settings);
            return Result.success("更新系统设置成功");
        } catch (Exception e) {
            return Result.error("更新系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 获取单个设置
     */
    @GetMapping("/{key}")
    public Result getSetting(@PathVariable String key) {
        try {
            String value = systemSettingsService.getSetting(key);
            return Result.success("获取设置成功", value);
        } catch (Exception e) {
            return Result.error("获取设置失败: " + e.getMessage());
        }
    }

    /**
     * 更新单个设置
     */
    @PutMapping("/{key}")
    public Result updateSetting(@PathVariable String key, @RequestBody Map<String, String> request) {
        try {
            String value = request.get("value");
            systemSettingsService.updateSetting(key, value);
            return Result.success("更新设置成功");
        } catch (Exception e) {
            return Result.error("更新设置失败: " + e.getMessage());
        }
    }
}