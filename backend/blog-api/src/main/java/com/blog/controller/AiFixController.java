package com.blog.controller;

import com.blog.entity.AiConfig;
import com.blog.service.AiConfigService;
import com.blog.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI功能修复控制器
 * 用于诊断和修复AI功能问题
 */
@Slf4j
@RestController
@RequestMapping("/api/ai-fix")
public class AiFixController {

    @Autowired
    private AiConfigService aiConfigService;

    @Autowired
    private AiService aiService;

    /**
     * 诊断AI功能问题
     */
    @GetMapping("/diagnose")
    public ResponseEntity<Map<String, Object>> diagnose() {
        Map<String, Object> diagnosis = new HashMap<>();
        Map<String, Object> issues = new HashMap<>();
        Map<String, Object> suggestions = new HashMap<>();

        try {
            // 检查AI功能是否启用
            boolean aiEnabled = aiConfigService.isAiEnabled();
            diagnosis.put("aiEnabled", aiEnabled);
            if (!aiEnabled) {
                issues.put("aiDisabled", "AI功能未启用");
                suggestions.put("enableAi", "需要在管理后台启用AI功能");
            }

            // 检查API配置
            Map<String, String> config = aiConfigService.getAiConfig();
            String apiKey = config.get("apiKey");
            String apiUrl = config.get("apiUrl");
            
            diagnosis.put("hasApiKey", apiKey != null && !apiKey.trim().isEmpty());
            diagnosis.put("apiUrl", apiUrl);
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                issues.put("noApiKey", "API密钥未配置");
                suggestions.put("configApiKey", "需要配置有效的AI服务API密钥");
            }

            if (apiUrl == null || apiUrl.trim().isEmpty()) {
                issues.put("noApiUrl", "API地址未配置");
                suggestions.put("configApiUrl", "需要配置AI服务API地址");
            }

            // 检查连接状态
            boolean connected = false;
            String connectionError = null;
            try {
                if (aiEnabled && apiKey != null && !apiKey.trim().isEmpty()) {
                    connected = aiService.checkApiConnection();
                }
            } catch (Exception e) {
                connectionError = e.getMessage();
            }
            
            diagnosis.put("connected", connected);
            diagnosis.put("connectionError", connectionError);
            
            if (!connected && aiEnabled) {
                issues.put("connectionFailed", "无法连接到AI服务");
                suggestions.put("checkConnection", "请检查API密钥和网络连接");
            }

            // 生成修复建议
            Map<String, Object> fixSteps = new HashMap<>();
            if (!aiEnabled) {
                fixSteps.put("step1", "启用AI功能：POST /api/ai-fix/enable-ai");
            }
            if (apiKey == null || apiKey.trim().isEmpty()) {
                fixSteps.put("step2", "配置API密钥：POST /api/ai-fix/set-api-key");
            }
            if (!connected && aiEnabled && apiKey != null && !apiKey.trim().isEmpty()) {
                fixSteps.put("step3", "测试连接：POST /api/ai-fix/test-connection");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("diagnosis", diagnosis);
            result.put("issues", issues);
            result.put("suggestions", suggestions);
            result.put("fixSteps", fixSteps);
            result.put("message", "诊断完成");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("AI功能诊断失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "诊断失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 一键修复AI功能
     */
    @PostMapping("/auto-fix")
    public ResponseEntity<Map<String, Object>> autoFix(@RequestBody Map<String, String> params) {
        try {
            String apiKey = params.get("apiKey");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "请提供API密钥进行修复");
                return ResponseEntity.ok(result);
            }

            Map<String, Object> fixResults = new HashMap<>();

            // 步骤1：启用AI功能
            boolean enableResult = aiConfigService.updateConfig(AiConfig.AI_ENABLED, "1");
            fixResults.put("enableAi", enableResult);

            // 步骤2：配置API密钥
            boolean keyResult = aiConfigService.updateConfig(AiConfig.AI_API_KEY, apiKey.trim());
            fixResults.put("setApiKey", keyResult);

            // 步骤3：设置其他默认配置
            Map<String, String> configs = new HashMap<>();
            configs.put(AiConfig.AI_API_URL, params.getOrDefault("apiUrl", "https://api.deepseek.com/v1/chat/completions"));
            configs.put(AiConfig.AI_MODEL, params.getOrDefault("model", "deepseek-chat"));
            configs.put(AiConfig.AI_MAX_TOKENS, "1800");
            configs.put(AiConfig.AI_TEMPERATURE, "0.6");
            
            boolean configResult = aiConfigService.updateConfigs(configs);
            fixResults.put("setConfigs", configResult);

            // 步骤4：测试连接
            boolean testResult = false;
            String testMessage = null;
            try {
                testResult = aiService.checkApiConnection();
                if (testResult) {
                    testMessage = aiService.sendSingleMessage("Hello", "Please respond with 'AI fix successful'");
                }
            } catch (Exception e) {
                testMessage = "测试失败：" + e.getMessage();
            }
            fixResults.put("testConnection", testResult);
            fixResults.put("testMessage", testMessage);

            boolean overallSuccess = enableResult && keyResult && configResult && testResult;

            Map<String, Object> result = new HashMap<>();
            result.put("success", overallSuccess);
            result.put("fixResults", fixResults);
            result.put("message", overallSuccess ? "AI功能修复成功" : "修复过程中遇到问题，请检查详细结果");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("自动修复AI功能失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "修复失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 启用AI功能
     */
    @PostMapping("/enable-ai")
    public ResponseEntity<Map<String, Object>> enableAi() {
        try {
            boolean success = aiConfigService.updateConfig(AiConfig.AI_ENABLED, "1");
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "AI功能已启用" : "启用失败");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("启用AI功能失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "启用失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 设置API密钥
     */
    @PostMapping("/set-api-key")
    public ResponseEntity<Map<String, Object>> setApiKey(@RequestBody Map<String, String> params) {
        try {
            String apiKey = params.get("apiKey");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "API密钥不能为空");
                return ResponseEntity.ok(result);
            }

            boolean success = aiConfigService.updateConfig(AiConfig.AI_API_KEY, apiKey.trim());
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("message", success ? "API密钥设置成功" : "设置失败");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("设置API密钥失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "设置失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 测试AI连接
     */
    @PostMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            boolean connected = aiService.checkApiConnection();
            String testResponse = null;
            
            if (connected) {
                testResponse = aiService.sendSingleMessage("测试消息", "请回复'连接测试成功'");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", connected);
            result.put("connected", connected);
            result.put("testResponse", testResponse);
            result.put("message", connected ? "连接测试成功" : "连接测试失败");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("测试连接失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("connected", false);
            result.put("message", "测试失败：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}