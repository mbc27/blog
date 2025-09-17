package com.blog.service.impl;

import com.blog.dto.AiWritingRequest;
import com.blog.service.AiService;
import com.blog.service.AiWritingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * AI写作助手服务实现
 */
@Service
@Slf4j
public class AiWritingServiceImpl implements AiWritingService {

    @Autowired
    private AiService aiService;

    @Override
    public String generateArticle(AiWritingRequest request) {
        String prompt = buildGenerateArticlePrompt(request);
        // 文章生成需要更多token，设置为4000
        return aiService.sendSingleMessage(prompt, null, 4000);
    }

    @Override
    public String continueWriting(AiWritingRequest request) {
        String prompt = buildContinueWritingPrompt(request);
        // 续写内容设置为3000 token
        return aiService.sendSingleMessage(prompt, null, 3000);
    }

    @Override
    public String optimizeContent(AiWritingRequest request) {
        String prompt = buildOptimizeContentPrompt(request);
        // 内容优化设置为2500 token
        return aiService.sendSingleMessage(prompt, null, 2500);
    }

    @Override
    public String generateOutline(AiWritingRequest request) {
        String prompt = buildGenerateOutlinePrompt(request);
        // 大纲生成设置为1500 token
        return aiService.sendSingleMessage(prompt, null, 1500);
    }

    @Override
    public String expandParagraph(AiWritingRequest request) {
        String prompt = buildExpandParagraphPrompt(request);
        // 段落扩展设置为2000 token
        return aiService.sendSingleMessage(prompt, null, 2000);
    }

    /**
     * 构建文章生成提示词
     */
    private String buildGenerateArticlePrompt(AiWritingRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("请根据以下要求创作一篇文章：\n\n");
        
        if (StringUtils.hasText(request.getTitle())) {
            prompt.append("标题：").append(request.getTitle()).append("\n");
        }
        
        if (StringUtils.hasText(request.getCategory())) {
            prompt.append("分类：").append(request.getCategory()).append("\n");
        }
        
        if (StringUtils.hasText(request.getTopic())) {
            prompt.append("主题描述：").append(request.getTopic()).append("\n");
        }
        
        // 写作风格
        String styleDesc = getStyleDescription(request.getStyle());
        if (StringUtils.hasText(styleDesc)) {
            prompt.append("写作风格：").append(styleDesc).append("\n");
        }
        
        // 内容长度
        String lengthDesc = getLengthDescription(request.getLength());
        if (StringUtils.hasText(lengthDesc)) {
            prompt.append("文章长度：").append(lengthDesc).append("\n");
        }
        
        prompt.append("\n要求：\n");
        prompt.append("1. 内容要有逻辑性和条理性\n");
        prompt.append("2. 语言要流畅自然\n");
        prompt.append("3. 适当使用Markdown格式\n");
        prompt.append("4. 内容要有价值和可读性\n");
        prompt.append("5. 请直接输出文章内容，不要包含额外的说明\n");
        
        return prompt.toString();
    }

    /**
     * 构建续写提示词
     */
    private String buildContinueWritingPrompt(AiWritingRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("请根据以下已有内容进行智能续写：\n\n");
        
        if (StringUtils.hasText(request.getContent())) {
            prompt.append("已有内容：\n").append(request.getContent()).append("\n\n");
        }
        
        if (StringUtils.hasText(request.getDirection())) {
            prompt.append("续写方向：").append(request.getDirection()).append("\n");
        }
        
        // 续写长度
        String lengthDesc = getLengthDescription(request.getLength());
        if (StringUtils.hasText(lengthDesc)) {
            prompt.append("续写长度：").append(lengthDesc).append("\n");
        }
        
        prompt.append("\n要求：\n");
        prompt.append("1. 保持与已有内容的连贯性和一致性\n");
        prompt.append("2. 延续原有的写作风格和语调\n");
        prompt.append("3. 内容要有逻辑性，自然过渡\n");
        prompt.append("4. 适当使用Markdown格式\n");
        prompt.append("5. 请直接输出续写内容，不要重复已有内容\n");
        
        return prompt.toString();
    }

    /**
     * 构建内容优化提示词
     */
    private String buildOptimizeContentPrompt(AiWritingRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("请对以下内容进行优化：\n\n");
        
        if (StringUtils.hasText(request.getContent())) {
            prompt.append("原始内容：\n").append(request.getContent()).append("\n\n");
        }
        
        // 优化类型
        if (request.getOptimizeTypes() != null && request.getOptimizeTypes().length > 0) {
            prompt.append("优化重点：\n");
            for (String type : request.getOptimizeTypes()) {
                switch (type) {
                    case "grammar":
                        prompt.append("- 语法修正：检查并修正语法错误\n");
                        break;
                    case "style":
                        prompt.append("- 文风优化：提升文章的表达效果和可读性\n");
                        break;
                    case "structure":
                        prompt.append("- 结构调整：优化文章的逻辑结构和段落组织\n");
                        break;
                    case "readability":
                        prompt.append("- 可读性提升：让内容更易理解和阅读\n");
                        break;
                }
            }
        }
        
        prompt.append("\n要求：\n");
        prompt.append("1. 保持原意不变的前提下进行优化\n");
        prompt.append("2. 提升语言的准确性和流畅性\n");
        prompt.append("3. 保持适当的Markdown格式\n");
        prompt.append("4. 请直接输出优化后的内容\n");
        
        return prompt.toString();
    }

    /**
     * 构建大纲生成提示词
     */
    private String buildGenerateOutlinePrompt(AiWritingRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("请根据以下信息生成文章大纲：\n\n");
        
        if (StringUtils.hasText(request.getTitle())) {
            prompt.append("文章标题：").append(request.getTitle()).append("\n");
        }
        
        if (StringUtils.hasText(request.getTopic())) {
            prompt.append("主题描述：").append(request.getTopic()).append("\n");
        }
        
        if (StringUtils.hasText(request.getCategory())) {
            prompt.append("文章分类：").append(request.getCategory()).append("\n");
        }
        
        prompt.append("\n要求：\n");
        prompt.append("1. 大纲要有清晰的层次结构\n");
        prompt.append("2. 每个部分要有简要的内容说明\n");
        prompt.append("3. 使用Markdown格式的标题层级\n");
        prompt.append("4. 大纲要完整且逻辑清晰\n");
        prompt.append("5. 请直接输出大纲内容\n");
        
        return prompt.toString();
    }

    /**
     * 构建段落扩展提示词
     */
    private String buildExpandParagraphPrompt(AiWritingRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("请对以下段落进行扩展和丰富：\n\n");
        
        if (StringUtils.hasText(request.getContent())) {
            prompt.append("原始段落：\n").append(request.getContent()).append("\n\n");
        }
        
        if (StringUtils.hasText(request.getDirection())) {
            prompt.append("扩展方向：").append(request.getDirection()).append("\n");
        }
        
        prompt.append("\n要求：\n");
        prompt.append("1. 保持原有段落的核心观点\n");
        prompt.append("2. 增加具体的例子、数据或论证\n");
        prompt.append("3. 让内容更加丰富和有说服力\n");
        prompt.append("4. 保持语言的流畅性\n");
        prompt.append("5. 请直接输出扩展后的段落内容\n");
        
        return prompt.toString();
    }

    /**
     * 获取写作风格描述
     */
    private String getStyleDescription(String style) {
        if (!StringUtils.hasText(style)) {
            return null;
        }
        
        switch (style) {
            case "technical":
                return "专业技术风格，使用准确的技术术语，逻辑严密";
            case "popular":
                return "通俗易懂风格，语言简洁明了，适合大众阅读";
            case "academic":
                return "学术严谨风格，论证充分，引用权威资料";
            case "humorous":
                return "轻松幽默风格，语言生动有趣，适当使用比喻";
            case "news":
                return "新闻报道风格，客观中立，重点突出，信息准确";
            default:
                return null;
        }
    }

    /**
     * 获取长度描述
     */
    private String getLengthDescription(String length) {
        if (!StringUtils.hasText(length)) {
            return null;
        }
        
        switch (length) {
            case "short":
                return "短篇（200-500字）";
            case "medium":
                return "中篇（500-1000字）";
            case "long":
                return "长篇（1000-2000字）";
            default:
                return null;
        }
    }
}