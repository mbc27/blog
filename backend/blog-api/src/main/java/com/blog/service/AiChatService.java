package com.blog.service;

import com.blog.entity.AiChatMessage;
import com.blog.entity.AiChatSession;

import java.util.List;

/**
 * AI聊天服务接口
 */
public interface AiChatService {

    /**
     * 创建新的聊天会话
     */
    AiChatSession createSession(Long userId, Integer sessionType, Long articleId);

    /**
     * 获取用户的聊天会话列表
     */
    List<AiChatSession> getUserSessions(Long userId);

    /**
     * 根据会话ID获取会话详情
     */
    AiChatSession getSessionBySessionId(String sessionId);

    /**
     * 获取会话的消息历史
     */
    List<AiChatMessage> getSessionMessages(Long sessionId);

    /**
     * 发送消息并获取AI回复
     */
    AiChatMessage sendMessage(String sessionId, String userMessage, Long userId);

    /**
     * 获取写作建议
     */
    String getWritingSuggestion(String content, String prompt);

    /**
     * 润色文章内容
     */
    String polishArticle(String content);

    /**
     * 基于博客内容回答问题
     */
    String answerBasedOnBlog(String question);

    /**
     * 结束会话
     */
    boolean endSession(String sessionId);

    /**
     * 删除会话
     */
    boolean deleteSession(String sessionId, Long userId);

    /**
     * 清理过期会话
     */
    void cleanExpiredSessions();
}