package com.blog.controller;

import com.blog.entity.AiChatMessage;
import com.blog.entity.AiChatSession;
import com.blog.service.AiChatService;
import com.blog.service.AiService;
import com.blog.utils.Result;
import com.blog.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/chat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;
    
    @Autowired
    private AiService aiService;

    /**
     * 创建新的聊天会话
     */
    @PostMapping("/session")
    public Result<AiChatSession> createSession(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            Integer sessionType = (Integer) params.get("sessionType");
            Long articleId = params.get("articleId") != null ? Long.valueOf(params.get("articleId").toString()) : null;

            if (sessionType == null) {
                sessionType = AiChatSession.TYPE_GENERAL;
            }

            AiChatSession session = aiChatService.createSession(userId, sessionType, articleId);
            return Result.success(session);
        } catch (Exception e) {
            log.error("创建AI会话失败", e);
            return Result.error("创建会话失败");
        }
    }

    /**
     * 获取用户的会话列表
     */
    @GetMapping("/sessions")
    public Result<List<AiChatSession>> getUserSessions(HttpServletRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            List<AiChatSession> sessions = aiChatService.getUserSessions(userId);
            return Result.success(sessions);
        } catch (Exception e) {
            log.error("获取用户会话列表失败", e);
            return Result.error("获取会话列表失败");
        }
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/session/{sessionId}")
    public Result<Map<String, Object>> getSessionDetail(@PathVariable String sessionId) {
        try {
            AiChatSession session = aiChatService.getSessionBySessionId(sessionId);
            if (session == null) {
                return Result.error("会话不存在");
            }

            List<AiChatMessage> messages = aiChatService.getSessionMessages(session.getId());
            
            Map<String, Object> result = new HashMap<>();
            result.put("session", session);
            result.put("messages", messages);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取会话详情失败", e);
            return Result.error("获取会话详情失败");
        }
    }

    /**
     * 发送消息
     */
    @PostMapping("/message")
    public Result<AiChatMessage> sendMessage(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String sessionId = (String) params.get("sessionId");
            String message = (String) params.get("message");
            
            if (sessionId == null || message == null || message.trim().isEmpty()) {
                return Result.error("参数不完整");
            }

            Long userId = SecurityUtils.getCurrentUserId();
            AiChatMessage response = aiChatService.sendMessage(sessionId, message, userId);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("发送AI消息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取写作建议
     */
    @PostMapping("/writing-suggestion")
    public Result<String> getWritingSuggestion(@RequestBody Map<String, String> params) {
        try {
            String content = params.get("content");
            String prompt = params.get("prompt");
            
            if (content == null || content.trim().isEmpty()) {
                return Result.error("内容不能为空");
            }

            String suggestion = aiChatService.getWritingSuggestion(content, prompt);
            return Result.success(suggestion);
        } catch (Exception e) {
            log.error("获取写作建议失败", e);
            return Result.error("获取写作建议失败");
        }
    }

    /**
     * 润色文章
     */
    @PostMapping("/polish")
    public Result<String> polishArticle(@RequestBody Map<String, String> params) {
        try {
            String content = params.get("content");
            
            if (content == null || content.trim().isEmpty()) {
                return Result.error("内容不能为空");
            }

            String polishedContent = aiChatService.polishArticle(content);
            return Result.success(polishedContent);
        } catch (Exception e) {
            log.error("润色文章失败", e);
            return Result.error("润色文章失败");
        }
    }

    /**
     * 基于博客内容回答问题
     */
    @PostMapping("/ask")
    public Result<String> askQuestion(@RequestBody Map<String, String> params) {
        try {
            String question = params.get("question");
            
            if (question == null || question.trim().isEmpty()) {
                return Result.error("问题不能为空");
            }

            String answer = aiChatService.answerBasedOnBlog(question);
            return Result.success(answer);
        } catch (Exception e) {
            log.error("AI问答失败", e);
            return Result.error("AI问答失败");
        }
    }

    /**
     * 结束会话
     */
    @PutMapping("/session/{sessionId}/end")
    public Result<Void> endSession(@PathVariable String sessionId) {
        try {
            boolean success = aiChatService.endSession(sessionId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("结束会话失败");
            }
        } catch (Exception e) {
            log.error("结束会话失败", e);
            return Result.error("结束会话失败");
        }
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/session/{sessionId}")
    public Result<Void> deleteSession(@PathVariable String sessionId, HttpServletRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            boolean success = aiChatService.deleteSession(sessionId, userId);
            if (success) {
                return Result.success();
            } else {
                return Result.error("删除会话失败");
            }
        } catch (Exception e) {
            log.error("删除会话失败", e);
            return Result.error("删除会话失败");
        }
    }
}

/**
 * AI测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
class AiTestController {

    @Autowired
    private AiService aiService;

    /**
     * 测试AI连接
     */
    @PostMapping("/test-connection")
    public Result<Map<String, Object>> testConnection(@RequestBody Map<String, Object> params) {
        try {
            boolean connected = aiService.checkApiConnection();
            Map<String, Object> result = new HashMap<>();
            result.put("connected", connected);
            result.put("message", connected ? "AI连接测试成功" : "AI连接测试失败");
            
            if (connected) {
                return Result.success(result);
            } else {
                return Result.error(500, "AI连接测试失败", result);
            }
        } catch (Exception e) {
            log.error("AI连接测试失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("connected", false);
            result.put("message", "AI连接测试失败：" + e.getMessage());
            return Result.error(500, "AI连接测试失败", result);
        }
    }
}