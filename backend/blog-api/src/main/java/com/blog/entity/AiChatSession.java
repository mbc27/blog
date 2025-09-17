package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI聊天会话实体类
 */
@Data
@TableName("ai_chat_session")
public class AiChatSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话标识符（UUID）
     */
    private String sessionId;

    /**
     * 用户ID（游客为null）
     */
    private Long userId;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 会话类型：1-普通对话，2-写作辅助，3-文章润色
     */
    private Integer sessionType;

    /**
     * 关联文章ID（写作辅助时使用）
     */
    private Long articleId;

    /**
     * 会话状态：1-活跃，0-已结束
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 会话类型常量
     */
    public static final int TYPE_GENERAL = 1;      // 普通对话
    public static final int TYPE_WRITING = 2;      // 写作辅助
    public static final int TYPE_POLISH = 3;       // 文章润色

    /**
     * 会话状态常量
     */
    public static final int STATUS_ACTIVE = 1;     // 活跃
    public static final int STATUS_ENDED = 0;      // 已结束
}