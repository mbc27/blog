package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.entity.*;
import com.blog.mapper.AiChatMessageMapper;
import com.blog.mapper.AiChatSessionMapper;
import com.blog.dao.ArticleMapper;
import com.blog.service.AiChatService;
import com.blog.service.AiConfigService;
import com.blog.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI聊天服务实现类
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiChatSessionMapper sessionMapper;

    @Autowired
    private AiChatMessageMapper messageMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private AiConfigService aiConfigService;

    @Autowired
    private AiService aiService;

    @Override
    @Transactional
    public AiChatSession createSession(Long userId, Integer sessionType, Long articleId) {
        AiChatSession session = new AiChatSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userId);
        session.setSessionType(sessionType);
        session.setArticleId(articleId);
        session.setStatus(AiChatSession.STATUS_ACTIVE);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());

        // 根据会话类型设置标题
        String title = generateSessionTitle(sessionType, articleId);
        session.setTitle(title);

        sessionMapper.insert(session);
        return session;
    }

    @Override
    public List<AiChatSession> getUserSessions(Long userId) {
        QueryWrapper<AiChatSession> wrapper = new QueryWrapper<>();
        if (userId != null) {
            wrapper.eq("user_id", userId);
        } else {
            wrapper.isNull("user_id");
        }
        wrapper.eq("status", AiChatSession.STATUS_ACTIVE);
        wrapper.orderByDesc("update_time");
        return sessionMapper.selectList(wrapper);
    }

    @Override
    public AiChatSession getSessionBySessionId(String sessionId) {
        return sessionMapper.selectBySessionId(sessionId);
    }

    @Override
    public List<AiChatMessage> getSessionMessages(Long sessionId) {
        QueryWrapper<AiChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId);
        wrapper.eq("status", AiChatMessage.STATUS_NORMAL);
        wrapper.orderByAsc("create_time");
        return messageMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public AiChatMessage sendMessage(String sessionId, String userMessage, Long userId) {
        try {
            // 获取会话信息
            AiChatSession session = getSessionBySessionId(sessionId);
            if (session == null) {
                throw new RuntimeException("会话不存在");
            }

            // 保存用户消息
            AiChatMessage userMsg = new AiChatMessage();
            userMsg.setSessionId(session.getId());
            userMsg.setRole(AiChatMessage.ROLE_USER);
            userMsg.setContent(userMessage);
            userMsg.setMessageType(AiChatMessage.TYPE_TEXT);
            userMsg.setStatus(AiChatMessage.STATUS_NORMAL);
            userMsg.setCreateTime(LocalDateTime.now());
            messageMapper.insert(userMsg);

            // 获取历史消息
            List<AiChatMessage> historyMessages = getSessionMessages(session.getId());
            
            // 构建对话上下文
            List<Map<String, String>> messages = historyMessages.stream()
                .filter(msg -> !msg.getRole().equals(AiChatMessage.ROLE_SYSTEM))
                .map(msg -> {
                    Map<String, String> message = new HashMap<>();
                    message.put("role", msg.getRole());
                    message.put("content", msg.getContent());
                    return message;
                })
                .collect(Collectors.toList());

            // 根据会话类型获取系统提示词
            String systemPrompt = getSystemPromptBySessionType(session.getSessionType(), session.getArticleId());

            // 调用AI服务获取回复
            String aiResponse = aiService.sendChatRequest(messages, systemPrompt);

            // 保存AI回复
            AiChatMessage aiMsg = new AiChatMessage();
            aiMsg.setSessionId(session.getId());
            aiMsg.setRole(AiChatMessage.ROLE_ASSISTANT);
            aiMsg.setContent(aiResponse);
            aiMsg.setMessageType(AiChatMessage.TYPE_TEXT);
            aiMsg.setStatus(AiChatMessage.STATUS_NORMAL);
            aiMsg.setCreateTime(LocalDateTime.now());
            messageMapper.insert(aiMsg);

            // 更新会话时间
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);

            return aiMsg;

        } catch (Exception e) {
            log.error("发送AI消息失败", e);
            throw new RuntimeException("AI服务暂时不可用，请稍后再试");
        }
    }

    @Override
    public String getWritingSuggestion(String content, String prompt) {
        String systemPrompt = aiConfigService.getConfigValue(AiConfig.AI_WRITING_PROMPT);
        String userMessage = "请对以下内容提供写作建议：\n\n" + content;
        if (prompt != null && !prompt.trim().isEmpty()) {
            userMessage += "\n\n具体要求：" + prompt;
        }
        return aiService.sendSingleMessage(userMessage, systemPrompt);
    }

    @Override
    public String polishArticle(String content) {
        String systemPrompt = aiConfigService.getConfigValue(AiConfig.AI_POLISH_PROMPT);
        String userMessage = "请润色以下文章内容：\n\n" + content;
        return aiService.sendSingleMessage(userMessage, systemPrompt);
    }

    @Override
    public String answerBasedOnBlog(String question) {
        // 这里可以实现基于博客内容的智能问答
        // 可以搜索相关文章内容，然后结合问题生成回答
        String systemPrompt = aiConfigService.getConfigValue(AiConfig.AI_SYSTEM_PROMPT) + 
            "\n你可以基于这个博客网站的内容来回答用户的问题。";
        
        // TODO: 实现文章内容搜索和相关性匹配
        // 这里先简单实现，后续可以加入向量搜索等高级功能
        
        return aiService.sendSingleMessage(question, systemPrompt);
    }

    @Override
    public boolean endSession(String sessionId) {
        return sessionMapper.updateSessionStatus(sessionId, AiChatSession.STATUS_ENDED) > 0;
    }

    @Override
    public boolean deleteSession(String sessionId, Long userId) {
        try {
            AiChatSession session = getSessionBySessionId(sessionId);
            if (session == null) {
                return false;
            }

            // 检查权限（游客只能删除自己的会话，用户只能删除自己的会话）
            if (userId != null && !userId.equals(session.getUserId())) {
                return false;
            }

            // 删除会话相关的消息
            QueryWrapper<AiChatMessage> messageWrapper = new QueryWrapper<>();
            messageWrapper.eq("session_id", session.getId());
            messageMapper.delete(messageWrapper);

            // 删除会话
            QueryWrapper<AiChatSession> sessionWrapper = new QueryWrapper<>();
            sessionWrapper.eq("session_id", sessionId);
            sessionMapper.delete(sessionWrapper);

            return true;
        } catch (Exception e) {
            log.error("删除会话失败", e);
            return false;
        }
    }

    @Override
    public void cleanExpiredSessions() {
        // 清理30天前的会话
        LocalDateTime expireTime = LocalDateTime.now().minusDays(30);
        
        QueryWrapper<AiChatSession> wrapper = new QueryWrapper<>();
        wrapper.lt("update_time", expireTime);
        
        List<AiChatSession> expiredSessions = sessionMapper.selectList(wrapper);
        
        for (AiChatSession session : expiredSessions) {
            deleteSession(session.getSessionId(), session.getUserId());
        }
        
        log.info("清理了 {} 个过期会话", expiredSessions.size());
    }

    /**
     * 根据会话类型生成标题
     */
    private String generateSessionTitle(Integer sessionType, Long articleId) {
        switch (sessionType) {
            case AiChatSession.TYPE_WRITING:
                return "写作辅助 - " + LocalDateTime.now().toString().substring(0, 16);
            case AiChatSession.TYPE_POLISH:
                if (articleId != null) {
                    Article article = articleMapper.selectById(articleId);
                    if (article != null) {
                        return "润色文章 - " + article.getTitle();
                    }
                }
                return "文章润色 - " + LocalDateTime.now().toString().substring(0, 16);
            default:
                return "AI对话 - " + LocalDateTime.now().toString().substring(0, 16);
        }
    }

    /**
     * 根据会话类型获取系统提示词
     */
    private String getSystemPromptBySessionType(Integer sessionType, Long articleId) {
        switch (sessionType) {
            case AiChatSession.TYPE_WRITING:
                return aiConfigService.getConfigValue(AiConfig.AI_WRITING_PROMPT);
            case AiChatSession.TYPE_POLISH:
                return aiConfigService.getConfigValue(AiConfig.AI_POLISH_PROMPT);
            default:
                return aiConfigService.getConfigValue(AiConfig.AI_SYSTEM_PROMPT);
        }
    }
}