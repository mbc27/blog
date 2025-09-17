package com.blog.service;

import java.util.List;
import java.util.Map;

/**
 * AI API服务接口
 */
public interface AiService {

    /**
     * 发送聊天请求到AI API
     */
    String sendChatRequest(List<Map<String, String>> messages);

    /**
     * 发送聊天请求到AI API（带系统提示）
     */
    String sendChatRequest(List<Map<String, String>> messages, String systemPrompt);

    /**
     * 发送单条消息请求
     */
    String sendSingleMessage(String message);

    /**
     * 发送单条消息请求（带系统提示）
     */
    String sendSingleMessage(String message, String systemPrompt);

    /**
     * 检查API连接状态
     */
    boolean checkApiConnection();

    /**
     * 获取模型信息
     */
    Map<String, Object> getModelInfo();
}