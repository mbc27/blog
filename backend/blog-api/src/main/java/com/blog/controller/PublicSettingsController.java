package com.blog.controller;

import com.blog.service.SystemSettingsService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共系统设置控制器（无需认证）
 */
@RestController
@RequestMapping("/api/settings")
@CrossOrigin
public class PublicSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    /**
     * 获取公共系统设置（无需认证）
     */
    @GetMapping("/public")
    public Result getPublicSettings() {
        try {
            Map<String, String> allSettings = systemSettingsService.getAllSettings();
            
            // 只返回公共可见的设置，不包含敏感信息
            Map<String, String> publicSettings = new HashMap<>();
            publicSettings.put("site_title", allSettings.getOrDefault("site_title", "博客系统"));
            publicSettings.put("site_description", allSettings.getOrDefault("site_description", "欢迎来到我的博客"));
            publicSettings.put("site_keywords", allSettings.getOrDefault("site_keywords", "blog,博客,技术分享"));
            publicSettings.put("site_author", allSettings.getOrDefault("site_author", "博主"));
            publicSettings.put("site_logo", allSettings.getOrDefault("site_logo", ""));
            publicSettings.put("site_favicon", allSettings.getOrDefault("site_favicon", ""));
            publicSettings.put("articles_per_page", allSettings.getOrDefault("articles_per_page", "10"));
            publicSettings.put("comments_per_page", allSettings.getOrDefault("comments_per_page", "10"));
            publicSettings.put("user_register", allSettings.getOrDefault("user_register", "1"));
            publicSettings.put("comment_audit", allSettings.getOrDefault("comment_audit", "1"));
            
            // 关于页面设置
            publicSettings.put("about_blog_intro", allSettings.getOrDefault("about_blog_intro", "这是一个基于Vue2和SpringBoot的个人博客系统，具有文章，表白墙，图片墙，收藏夹，音乐播放，视频播放，留言，友链，后台管理等功能。"));
            publicSettings.put("about_tech_stack", allSettings.getOrDefault("about_tech_stack", "本网站采用前后端分离进行实现，前端使用Vue2和Element UI，后端使用Java SpringBoot和MySQL。"));
            publicSettings.put("about_author", allSettings.getOrDefault("about_author", "热爱编程，喜欢分享技术知识和生活感悟。希望通过这个博客平台与大家交流学习，共同进步。"));
            publicSettings.put("about_contact", allSettings.getOrDefault("about_contact", "如果您有任何问题或建议，欢迎通过留言板与我联系，我会尽快回复。"));
            
            return Result.success("获取公共设置成功", publicSettings);
        } catch (Exception e) {
            return Result.error("获取公共设置失败: " + e.getMessage());
        }
    }
}