package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI配置实体类
 */
@Data
@TableName("ai_config")
public class AiConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置键
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置描述
     */
    private String description;

    /**
     * 配置分组
     */
    private String configGroup;

    /**
     * 是否启用：1-启用，0-禁用
     */
    private Integer enabled;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 配置键常量
     */
    public static final String AI_ENABLED = "ai.enabled";
    public static final String AI_API_KEY = "ai.api.key";
    public static final String AI_API_SECRET = "ai.api.secret";  // 讯飞星火API Secret
    public static final String AI_APP_ID = "ai.app.id";         // 讯飞星火App ID
    public static final String AI_API_URL = "ai.api.url";
    public static final String AI_MODEL = "ai.model";
    public static final String AI_PROVIDER = "ai.provider";
    public static final String AI_MAX_TOKENS = "ai.max.tokens";
    public static final String AI_TEMPERATURE = "ai.temperature";
    public static final String AI_API_TIMEOUT = "ai.api.timeout";
    public static final String AI_SYSTEM_PROMPT = "ai.system.prompt";
    public static final String AI_WRITING_PROMPT = "ai.writing.prompt";
    public static final String AI_POLISH_PROMPT = "ai.polish.prompt";

    /**
     * 配置分组常量
     */
    public static final String GROUP_BASIC = "basic";
    public static final String GROUP_API = "api";
    public static final String GROUP_PARAMETERS = "parameters";
    public static final String GROUP_PROMPTS = "prompts";
}