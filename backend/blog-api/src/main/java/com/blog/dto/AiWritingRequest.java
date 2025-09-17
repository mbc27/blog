package com.blog.dto;

import lombok.Data;

/**
 * AI写作请求DTO
 */
@Data
public class AiWritingRequest {
    
    /**
     * 操作类型：generate_article, continue_writing, optimize_content, generate_outline, expand_paragraph
     */
    private String type;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章分类
     */
    private String category;
    
    /**
     * 主题描述或提示词
     */
    private String topic;
    
    /**
     * 现有内容（用于续写或优化）
     */
    private String content;
    
    /**
     * 写作风格：technical, popular, academic, humorous, news
     */
    private String style;
    
    /**
     * 内容长度：short, medium, long
     */
    private String length;
    
    /**
     * 续写方向或优化要求
     */
    private String direction;
    
    /**
     * 优化类型：grammar, style, structure, readability
     */
    private String[] optimizeTypes;
    
    /**
     * 语言：zh-CN, en-US
     */
    private String language = "zh-CN";
}