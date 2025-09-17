package com.blog.controller;

import com.blog.entity.AiConfig;
import com.blog.service.AiConfigService;
import com.blog.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI配置管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/ai-config")
public class AiConfigController {

    @Autowired
    private AiConfigService aiConfigService;

    @Autowired
    private AiService aiService;

    /**
     * 获取所有AI配置
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllConfigs() {
        try {
            List<AiConfig> configs = aiConfigService.getAllConfigs();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", configs);
            result.put("message", "获取配置成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取AI配置失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取配置失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取AI配置状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAiStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("enabled", aiConfigService.isAiEnabled());
            status.put("config", aiConfigService.getAiConfig());
            status.put("modelInfo", aiService.getModelInfo());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", status);
            result.put("message", "获取状态成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取AI状态失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取状态失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 更新AI配置
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateConfigs(@RequestBody Map<String, String> configs) {
        try {
            boolean success = aiConfigService.updateConfigs(configs);
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "配置更新成功" : "配置更新失败");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("更新AI配置失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "更新配置失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 测试AI连接
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            boolean connected = aiService.checkApiConnection();
            String testMessage = aiService.sendSingleMessage("Hello", "Please respond with 'AI connection test successful'");
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", connected);
            result.put("connected", connected);
            result.put("testResponse", testMessage);
            result.put("message", connected ? "AI连接测试成功" : "AI连接测试失败");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("测试AI连接失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("connected", false);
            result.put("message", "连接测试失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 快速启用AI功能（使用默认配置）
     */
    @PostMapping("/quick-enable")
    public ResponseEntity<Map<String, Object>> quickEnable(@RequestBody Map<String, String> params) {
        try {
            String apiKey = params.get("apiKey");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "请提供API密钥");
                return ResponseEntity.ok(result);
            }

            // 更新基本配置
            Map<String, String> configs = new HashMap<>();
            configs.put(AiConfig.AI_ENABLED, "1");
            configs.put(AiConfig.AI_API_KEY, apiKey.trim());
            configs.put(AiConfig.AI_API_URL, params.getOrDefault("apiUrl", "https://api.deepseek.com/v1/chat/completions"));
            configs.put(AiConfig.AI_MODEL, params.getOrDefault("model", "deepseek-chat"));
            configs.put(AiConfig.AI_MAX_TOKENS, params.getOrDefault("maxTokens", "1800"));
            configs.put(AiConfig.AI_TEMPERATURE, params.getOrDefault("temperature", "0.6"));

            boolean success = aiConfigService.updateConfigs(configs);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "AI功能启用成功" : "AI功能启用失败");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("快速启用AI功能失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "启用失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 禁用AI功能
     */
    @PostMapping("/disable")
    public ResponseEntity<Map<String, Object>> disableAi() {
        try {
            boolean success = aiConfigService.updateConfig(AiConfig.AI_ENABLED, "0");
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "AI功能已禁用" : "禁用失败");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("禁用AI功能失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "禁用失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 重置AI配置为默认值
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetConfigs() {
        try {
            // 重新初始化默认配置
            aiConfigService.initDefaultConfigs();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "配置已重置为默认值");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("重置AI配置失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "重置失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}