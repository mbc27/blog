package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.entity.AiConfig;
import com.blog.mapper.AiConfigMapper;
import com.blog.service.AiConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI配置服务实现类
 */
@Slf4j
@Service
public class AiConfigServiceImpl implements AiConfigService {

    @Autowired
    private AiConfigMapper aiConfigMapper;

    @PostConstruct
    public void init() {
        initDefaultConfigs();
    }

    @Override
    public List<AiConfig> getAllConfigs() {
        return aiConfigMapper.selectList(new QueryWrapper<AiConfig>().orderByAsc("config_group", "sort_order"));
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        AiConfig config = aiConfigMapper.selectByConfigKey(configKey);
        if (config != null && config.getEnabled() == 1) {
            return config.getConfigValue();
        }
        return defaultValue;
    }

    @Override
    public List<AiConfig> getConfigsByGroup(String configGroup) {
        return aiConfigMapper.selectByConfigGroup(configGroup);
    }

    @Override
    public boolean updateConfig(String configKey, String configValue) {
        AiConfig config = aiConfigMapper.selectByConfigKey(configKey);
        if (config != null) {
            config.setConfigValue(configValue);
            config.setUpdateTime(LocalDateTime.now());
            return aiConfigMapper.updateById(config) > 0;
        }
        return false;
    }

    @Override
    public boolean updateConfigs(Map<String, String> configs) {
        try {
            for (Map.Entry<String, String> entry : configs.entrySet()) {
                updateConfig(entry.getKey(), entry.getValue());
            }
            return true;
        } catch (Exception e) {
            log.error("批量更新AI配置失败", e);
            return false;
        }
    }

    @Override
    public boolean createConfig(AiConfig config) {
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        return aiConfigMapper.insert(config) > 0;
    }

    @Override
    public boolean deleteConfig(String configKey) {
        QueryWrapper<AiConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("config_key", configKey);
        return aiConfigMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean isAiEnabled() {
        String enabled = getConfigValue(AiConfig.AI_ENABLED, "0");
        return "1".equals(enabled);
    }

    @Override
    public Map<String, String> getAiConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("ai.api.key", getConfigValue(AiConfig.AI_API_KEY, ""));
        config.put("ai.api.url", getConfigValue(AiConfig.AI_API_URL, "https://api.deepseek.com/v1/chat/completions"));
        config.put("ai.model", getConfigValue(AiConfig.AI_MODEL, "deepseek-chat"));
        config.put("ai.max.tokens", getConfigValue(AiConfig.AI_MAX_TOKENS, "1800"));
        config.put("ai.temperature", getConfigValue(AiConfig.AI_TEMPERATURE, "0.6"));
        config.put("ai.provider", getConfigValue(AiConfig.AI_PROVIDER, "deepseek"));
        return config;
    }

    @Override
    public void initDefaultConfigs() {
        // 检查是否已经初始化过
        AiConfig existingConfig = aiConfigMapper.selectByConfigKey(AiConfig.AI_ENABLED);
        if (existingConfig != null) {
            return;
        }

        log.info("初始化AI默认配置...");

        // 基础配置
        createDefaultConfig(AiConfig.AI_ENABLED, "0", "AI功能启用", "是否启用AI助手功能：1-启用，0-禁用", AiConfig.GROUP_BASIC, 1);
        createDefaultConfig(AiConfig.AI_PROVIDER, "deepseek", "AI服务提供商", "当前使用的AI服务提供商", AiConfig.GROUP_BASIC, 2);

        // API配置
        createDefaultConfig(AiConfig.AI_API_KEY, "", "AI API密钥", "AI服务的API密钥，请在管理后台配置", AiConfig.GROUP_API, 1);
        createDefaultConfig(AiConfig.AI_API_URL, "https://api.deepseek.com/v1/chat/completions", "AI API地址", "AI服务的API请求地址", AiConfig.GROUP_API, 2);
        createDefaultConfig(AiConfig.AI_MODEL, "deepseek-chat", "AI模型", "使用的AI模型名称", AiConfig.GROUP_API, 3);
        createDefaultConfig(AiConfig.AI_API_TIMEOUT, "30000", "API超时时间", "API请求超时时间(毫秒)", AiConfig.GROUP_API, 4);

        // 参数配置
        createDefaultConfig(AiConfig.AI_MAX_TOKENS, "1800", "最大Token数", "AI回复的最大Token数量", AiConfig.GROUP_PARAMETERS, 1);
        createDefaultConfig(AiConfig.AI_TEMPERATURE, "0.6", "温度参数", "AI回复的创造性程度，0-1之间", AiConfig.GROUP_PARAMETERS, 2);

        // 提示词配置
        createDefaultConfig(AiConfig.AI_SYSTEM_PROMPT, 
            "你是一个智能的博客助手，可以帮助用户解答问题、提供写作建议。请用友好、专业的语气回答用户的问题。", 
            "系统提示词", "AI助手的系统提示词", AiConfig.GROUP_PROMPTS, 1);
        
        createDefaultConfig(AiConfig.AI_WRITING_PROMPT, 
            "你是一个专业的写作助手，请根据用户提供的内容和要求，提供具体的写作建议和改进意见。", 
            "写作助手提示词", "写作辅助功能的提示词", AiConfig.GROUP_PROMPTS, 2);
        
        createDefaultConfig(AiConfig.AI_POLISH_PROMPT, 
            "你是一个专业的文章编辑，请对用户提供的文章内容进行润色，改善语言表达、逻辑结构和可读性。", 
            "文章润色提示词", "文章润色功能的提示词", AiConfig.GROUP_PROMPTS, 3);

        log.info("AI默认配置初始化完成");
    }

    private void createDefaultConfig(String configKey, String configValue, String configName, 
                                   String description, String configGroup, Integer sortOrder) {
        AiConfig config = new AiConfig();
        config.setConfigKey(configKey);
        config.setConfigValue(configValue);
        config.setConfigName(configName);
        config.setDescription(description);
        config.setConfigGroup(configGroup);
        config.setEnabled(1);
        config.setSortOrder(sortOrder);
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        
        aiConfigMapper.insert(config);
    }
}