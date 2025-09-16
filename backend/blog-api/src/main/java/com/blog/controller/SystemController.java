package com.blog.controller;

import com.blog.util.UrlDetectionUtils;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统信息控制器
 * 提供系统相关的API接口
 */
@RestController
@RequestMapping("/api/system")
@CrossOrigin
public class SystemController {

    @Autowired
    private UrlDetectionUtils urlDetectionUtils;

    /**
     * 获取当前服务器信息
     * 供前端动态获取服务器地址
     */
    @GetMapping("/server-info")
    public Result getServerInfo() {
        Map<String, String> serverInfo = new HashMap<>();
        serverInfo.put("baseUrl", urlDetectionUtils.getCurrentBaseUrl());
        serverInfo.put("uploadUrlPrefix", urlDetectionUtils.getCurrentUploadUrlPrefix());
        
        return Result.success(serverInfo);
    }
    
    /**
     * 检查服务器状态
     */
    @GetMapping("/health")
    public Result health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("baseUrl", urlDetectionUtils.getCurrentBaseUrl());
        
        return Result.success(health);
    }
    
    /**
     * 获取当前服务器IP地址
     * 供前端动态获取服务器IP地址，用于图片URL处理
     */
    @GetMapping("/ip")
    public Result getServerIp() {
        String ipAddress = urlDetectionUtils.getServerIpAddress();
        return Result.success(ipAddress);
    }
}