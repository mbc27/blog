package com.blog.service.impl;

import com.blog.service.AiConfigService;
import com.blog.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 通用AI服务实现类
 * 支持多种AI服务提供商的HTTP API调用
 */
@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiConfigService aiConfigService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String sendChatRequest(List<Map<String, String>> messages) {
        return sendChatRequest(messages, null);
    }

    @Override
    public String sendChatRequest(List<Map<String, String>> messages, String systemPrompt) {
        return sendChatRequest(messages, systemPrompt, null);
    }

    public String sendChatRequest(List<Map<String, String>> messages, String systemPrompt, Integer maxTokens) {
        try {
            // 检查AI功能是否启用
            if (!aiConfigService.isAiEnabled()) {
                log.warn("AI功能未启用");
                return "AI助手功能当前已关闭，如需使用请联系管理员开启。";
            }

            Map<String, String> config = aiConfigService.getAiConfig();
            String apiKey = config.get("ai.api.key");
            String apiUrl = config.get("ai.api.url");
            
            log.info("AI配置信息 - apiKey: {}, apiUrl: {}", 
                apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) + "..." : "null", 
                apiUrl);
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("AI API密钥未配置");
                return "AI服务配置不完整，请联系管理员。";
            }

            // 构建请求消息列表
            List<Map<String, String>> requestMessages = new ArrayList<>();
            
            // 添加系统提示词
            if (systemPrompt != null && !systemPrompt.trim().isEmpty()) {
                Map<String, String> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                requestMessages.add(systemMessage);
            }
            
            // 添加对话消息
            requestMessages.addAll(messages);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.get("ai.model"));
            requestBody.put("messages", requestMessages);
            // 使用自定义maxTokens，如果没有则使用配置值，最后使用默认值
            int tokenLimit = maxTokens != null ? maxTokens : Integer.parseInt(config.getOrDefault("ai.max.tokens", "2000"));
            requestBody.put("max_tokens", tokenLimit);
            requestBody.put("temperature", Double.parseDouble(config.getOrDefault("ai.temperature", "0.7")));
            requestBody.put("stream", false);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            log.info("发送AI请求到: {}", apiUrl);
            log.debug("请求体: {}", requestBody);
            
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);
            
            log.info("AI API响应状态码: {}", response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                String result = parseResponse(response.getBody());
                log.info("AI响应解析成功，内容长度: {}", result != null ? result.length() : 0);
                return result;
            } else {
                log.error("AI API请求失败，状态码: {}, 响应体: {}", response.getStatusCode(), response.getBody());
                return "AI服务暂时不可用，请稍后再试。";
            }

        } catch (Exception e) {
            log.error("调用AI API失败，详细错误: {}", e.getMessage(), e);
            return "AI服务出现错误，请稍后再试。";
        }
    }

    @Override
    public String sendSingleMessage(String message) {
        return sendSingleMessage(message, null);
    }

    @Override
    public String sendSingleMessage(String message, String systemPrompt) {
        return sendSingleMessage(message, systemPrompt, null);
    }

    @Override
    public String sendSingleMessage(String message, String systemPrompt, Integer maxTokens) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.add(userMessage);
        
        return sendChatRequest(messages, systemPrompt, maxTokens);
    }

    @Override
    public boolean checkApiConnection() {
        try {
            Map<String, String> config = aiConfigService.getAiConfig();
            String apiKey = config.get("ai.api.key");
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return false;
            }

            // 发送一个简单的测试请求
            String testResponse = sendSingleMessage("Hello", "You are a helpful assistant. Please respond with 'OK' only.");
            return testResponse != null && !testResponse.contains("错误") && !testResponse.contains("失败");
            
        } catch (Exception e) {
            log.error("检查AI API连接失败", e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getModelInfo() {
        Map<String, Object> modelInfo = new HashMap<>();
        Map<String, String> config = aiConfigService.getAiConfig();
        
        modelInfo.put("model", config.get("ai.model"));
        modelInfo.put("maxTokens", config.get("ai.max.tokens"));
        modelInfo.put("temperature", config.get("ai.temperature"));
        modelInfo.put("apiUrl", config.get("ai.api.url"));
        modelInfo.put("connected", checkApiConnection());
        
        return modelInfo;
    }

    /**
     * 解析AI API响应
     */
    private String parseResponse(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode choices = jsonNode.get("choices");
            
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                if (message != null) {
                    JsonNode content = message.get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
            
            // 如果解析失败，返回错误信息
            JsonNode error = jsonNode.get("error");
            if (error != null) {
                String errorMessage = error.get("message").asText();
                log.error("AI API返回错误: {}", errorMessage);
                return "AI服务返回错误: " + errorMessage;
            }
            
            return "AI服务响应格式异常。";
            
        } catch (Exception e) {
            log.error("解析AI API响应失败", e);
            return "AI服务响应解析失败。";
        }
    }
}