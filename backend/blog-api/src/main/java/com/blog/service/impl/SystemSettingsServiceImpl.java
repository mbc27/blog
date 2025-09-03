package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.blog.entity.SystemSettings;
import com.blog.mapper.SystemSettingsMapper;
import com.blog.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置服务实现类
 */
@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {
    
    @Autowired
    private SystemSettingsMapper systemSettingsMapper;
    
    @Override
    public Map<String, String> getAllSettings() {
        List<SystemSettings> settingsList = systemSettingsMapper.selectList(null);
        Map<String, String> settingsMap = new HashMap<>();
        
        for (SystemSettings setting : settingsList) {
            settingsMap.put(setting.getSettingKey(), setting.getSettingValue());
        }
        
        return settingsMap;
    }
    
    @Override
    public String getSetting(String key) {
        QueryWrapper<SystemSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setting_key", key);
        SystemSettings setting = systemSettingsMapper.selectOne(queryWrapper);
        
        return setting != null ? setting.getSettingValue() : null;
    }
    
    @Override
    public String getSetting(String key, String defaultValue) {
        String value = getSetting(key);
        return value != null ? value : defaultValue;
    }
    
    @Override
    public void updateSetting(String key, String value) {
        QueryWrapper<SystemSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setting_key", key);
        SystemSettings existingSetting = systemSettingsMapper.selectOne(queryWrapper);
        
        if (existingSetting != null) {
            // 更新现有设置
            UpdateWrapper<SystemSettings> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("setting_key", key);
            updateWrapper.set("setting_value", value);
            updateWrapper.set("update_time", LocalDateTime.now());
            systemSettingsMapper.update(null, updateWrapper);
        } else {
            // 创建新设置
            SystemSettings newSetting = new SystemSettings();
            newSetting.setSettingKey(key);
            newSetting.setSettingValue(value);
            newSetting.setCreateTime(LocalDateTime.now());
            newSetting.setUpdateTime(LocalDateTime.now());
            systemSettingsMapper.insert(newSetting);
        }
    }
    
    @Override
    public void updateSettings(Map<String, String> settings) {
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            updateSetting(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public void deleteSetting(String key) {
        QueryWrapper<SystemSettings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("setting_key", key);
        systemSettingsMapper.delete(queryWrapper);
    }
}