package com.blog.controller;

import com.blog.utils.Result;
import com.blog.dto.AiWritingRequest;
import com.blog.service.AiWritingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI写作助手控制器
 */
@RestController
@RequestMapping("/api/ai/writing")
@Slf4j
public class AiWritingController {

    @Autowired
    private AiWritingService aiWritingService;

    @PostMapping("/generate-article")
    public Result generateArticle(@RequestBody AiWritingRequest request) {
        try {
            log.info("收到文章生成请求: {}", request);
            String result = aiWritingService.generateArticle(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("生成文章失败", e);
            return Result.error("生成文章失败: " + e.getMessage());
        }
    }

    @PostMapping("/continue-writing")
    public Result continueWriting(@RequestBody AiWritingRequest request) {
        try {
            log.info("收到续写请求: {}", request);
            String result = aiWritingService.continueWriting(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("续写内容失败", e);
            return Result.error("续写内容失败: " + e.getMessage());
        }
    }

    @PostMapping("/optimize-content")
    public Result optimizeContent(@RequestBody AiWritingRequest request) {
        try {
            log.info("收到内容优化请求: {}", request);
            String result = aiWritingService.optimizeContent(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("优化内容失败", e);
            return Result.error("优化内容失败: " + e.getMessage());
        }
    }

    @PostMapping("/generate-outline")
    public Result generateOutline(@RequestBody AiWritingRequest request) {
        try {
            log.info("收到大纲生成请求: {}", request);
            String result = aiWritingService.generateOutline(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("生成大纲失败", e);
            return Result.error("生成大纲失败: " + e.getMessage());
        }
    }

    @PostMapping("/expand-paragraph")
    public Result expandParagraph(@RequestBody AiWritingRequest request) {
        try {
            log.info("收到段落扩展请求: {}", request);
            String result = aiWritingService.expandParagraph(request);
            return Result.success(result);
        } catch (Exception e) {
            log.error("扩展段落失败", e);
            return Result.error("扩展段落失败: " + e.getMessage());
        }
    }
}