package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI聊天消息实体类
 */
@Data
@TableName("ai_chat_message")
public class AiChatMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 消息角色：user-用户，assistant-AI助手，system-系统
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：1-文本，2-图片，3-文件
     */
    private Integer messageType;

    /**
     * 消息状态：1-正常，0-已删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 消息角色常量
     */
    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";
    public static final String ROLE_SYSTEM = "system";

    /**
     * 消息类型常量
     */
    public static final int TYPE_TEXT = 1;         // 文本
    public static final int TYPE_IMAGE = 2;        // 图片
    public static final int TYPE_FILE = 3;         // 文件

    /**
     * 消息状态常量
     */
    public static final int STATUS_NORMAL = 1;     // 正常
    public static final int STATUS_DELETED = 0;    // 已删除
}