package com.blog.controller;

import com.blog.entity.AiConfig;
import com.blog.service.AiConfigService;
import com.blog.service.AiService;
import com.blog.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理端AI配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/ai")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAiConfigController {

    @Autowired
    private AiConfigService aiConfigService;

    @Autowired
    private AiService aiService;

    /**
     * 获取所有AI配置
     */
    @GetMapping("/configs")
    public Result<List<AiConfig>> getAllConfigs() {
        try {
            List<AiConfig> configs = aiConfigService.getAllConfigs();
            return Result.success(configs);
        } catch (Exception e) {
            log.error("获取AI配置失败", e);
            return Result.error("获取配置失败");
        }
    }

    /**
     * 根据分组获取配置
     */
    @GetMapping("/configs/{group}")
    public Result<List<AiConfig>> getConfigsByGroup(@PathVariable String group) {
        try {
            List<AiConfig> configs = aiConfigService.getConfigsByGroup(group);
            return Result.success(configs);
        } catch (Exception e) {
            log.error("获取AI配置分组失败", e);
            return Result.error("获取配置失败");
        }
    }

    /**
     * 更新单个配置
     */
    @PutMapping("/config")
    public Result<Void> updateConfig(@RequestBody Map<String, String> params) {
        try {
            String configKey = params.get("configKey");
            String configValue = params.get("configValue");
            
            if (configKey == null || configValue == null) {
                return Result.error("参数不完整");
            }

            boolean success = aiConfigService.updateConfig(configKey, configValue);
            if (success) {
                return Result.success();
            } else {
                return Result.error("更新配置失败");
            }
        } catch (Exception e) {
            log.error("更新AI配置失败", e);
            return Result.error("更新配置失败");
        }
    }

    /**
     * 批量更新配置
     */
    @PutMapping("/configs")
    public Result<Void> updateConfigs(@RequestBody Map<String, String> configs) {
        try {
            boolean success = aiConfigService.updateConfigs(configs);
            if (success) {
                return Result.success();
            } else {
                return Result.error("批量更新配置失败");
            }
        } catch (Exception e) {
            log.error("批量更新AI配置失败", e);
            return Result.error("批量更新配置失败");
        }
    }

    /**
     * 创建新配置
     */
    @PostMapping("/config")
    public Result<Void> createConfig(@RequestBody AiConfig config) {
        try {
            boolean success = aiConfigService.createConfig(config);
            if (success) {
                return Result.success();
            } else {
                return Result.error("创建配置失败");
            }
        } catch (Exception e) {
            log.error("创建AI配置失败", e);
            return Result.error("创建配置失败");
        }
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/config/{configKey}")
    public Result<Void> deleteConfig(@PathVariable String configKey) {
        try {
            boolean success = aiConfigService.deleteConfig(configKey);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除配置失败");
            }
        } catch (Exception e) {
            log.error("删除AI配置失败", e);
            return Result.error("删除配置失败");
        }
    }

    /**
     * 测试API连接
     */
    @PostMapping("/test-connection")
    public Result<Map<String, Object>> testConnection() {
        try {
            boolean connected = aiService.checkApiConnection();
            Map<String, Object> result = new HashMap<>();
            result.put("connected", connected);
            result.put("message", connected ? "API连接正常" : "API连接失败，请检查配置");
            
            if (connected) {
                result.put("modelInfo", aiService.getModelInfo());
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("测试API连接失败", e);
            return Result.error("测试连接失败");
        }
    }

    /**
     * 获取AI功能状态
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getAiStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("enabled", aiConfigService.isAiEnabled());
            status.put("connected", aiService.checkApiConnection());
            status.put("config", aiConfigService.getAiConfig());
            
            return Result.success(status);
        } catch (Exception e) {
            log.error("获取AI状态失败", e);
            return Result.error("获取AI状态失败");
        }
    }

    /**
     * 重新初始化默认配置
     */
    @PostMapping("/init-defaults")
    public Result<Void> initDefaultConfigs() {
        try {
            aiConfigService.initDefaultConfigs();
            return Result.success();
        } catch (Exception e) {
            log.error("初始化默认配置失败", e);
            return Result.error("初始化默认配置失败");
        }
    }
}