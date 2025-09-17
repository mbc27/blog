package com.blog.service;

import com.blog.dto.AiWritingRequest;

/**
 * AI写作助手服务接口
 */
public interface AiWritingService {
    
    /**
     * 根据主题生成文章
     */
    String generateArticle(AiWritingRequest request);
    
    /**
     * 智能续写内容
     */
    String continueWriting(AiWritingRequest request);
    
    /**
     * 优化文章内容
     */
    String optimizeContent(AiWritingRequest request);
    
    /**
     * 生成文章大纲
     */
    String generateOutline(AiWritingRequest request);
    
    /**
     * 扩展段落内容
     */
    String expandParagraph(AiWritingRequest request);
}