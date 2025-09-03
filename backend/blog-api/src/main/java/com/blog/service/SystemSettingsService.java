package com.blog.service;

import java.util.Map;

/**
 * 系统设置服务接口
 */
public interface SystemSettingsService {
    
    /**
     * 获取所有系统设置
     */
    Map<String, String> getAllSettings();
    
    /**
     * 获取单个设置
     */
    String getSetting(String key);
    
    /**
     * 获取单个设置，如果不存在则返回默认值
     */
    String getSetting(String key, String defaultValue);
    
    /**
     * 更新单个设置
     */
    void updateSetting(String key, String value);
    
    /**
     * 批量更新设置
     */
    void updateSettings(Map<String, String> settings);
    
    /**
     * 删除设置
     */
    void deleteSetting(String key);
}