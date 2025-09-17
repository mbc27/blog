package com.blog.service;

import com.blog.entity.AiConfig;

import java.util.List;
import java.util.Map;

/**
 * AI配置服务接口
 */
public interface AiConfigService {

    /**
     * 获取所有AI配置
     */
    List<AiConfig> getAllConfigs();

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值，如果不存在返回默认值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 根据配置分组获取配置
     */
    List<AiConfig> getConfigsByGroup(String configGroup);

    /**
     * 更新配置
     */
    boolean updateConfig(String configKey, String configValue);

    /**
     * 批量更新配置
     */
    boolean updateConfigs(Map<String, String> configs);

    /**
     * 创建配置
     */
    boolean createConfig(AiConfig config);

    /**
     * 删除配置
     */
    boolean deleteConfig(String configKey);

    /**
     * 检查AI功能是否启用
     */
    boolean isAiEnabled();

    /**
     * 获取AI API配置
     */
    Map<String, String> getAiConfig();

    /**
     * 初始化默认配置
     */
    void initDefaultConfigs();
}