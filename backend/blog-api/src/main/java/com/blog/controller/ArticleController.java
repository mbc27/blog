package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Article;
import com.blog.service.ArticleService;
import com.blog.utils.Result;
import com.blog.utils.SecurityUtils;
import com.blog.vo.ArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public Result getArticle(@PathVariable Long id) {
        try {
            ArticleVo article = articleService.getArticleById(id);
            if (article == null) {
                return Result.error("文章不存在");
            }
            
            // 获取当前用户ID
            Long currentUserId = SecurityUtils.getCurrentUserId();
            boolean isAdmin = SecurityUtils.isAdmin();
            
            // 检查文章状态和权限
            if (article.getStatus() != 1) { // 非已发布状态
                // 如果不是管理员且不是作者，则无权查看
                if (!isAdmin && (currentUserId == null || !currentUserId.equals(article.getUserId()))) {
                    return Result.error("您无权查看该文章");
                }
            }
            
            // 增加浏览量
            articleService.incrementViewCount(id);
            return Result.success(article);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增文章（所有登录用户都可以发布，普通用户发布需要审核）
     *
     * @param article 文章信息
     * @return 新增结果
     */
    @PostMapping
    public Result addArticle(@RequestBody Article article) {
        try {
            boolean result = articleService.addArticle(article);
            if (result) {
                return Result.success("发布成功，等待审核");
            } else {
                return Result.error("发布失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页查询文章列表（所有已发布的文章）
     *
     * @param current    当前页码
     * @param size       每页大小
     * @param categoryId 分类ID
     * @return 文章列表
     */
    @GetMapping("/list")
    public Result listArticles(@RequestParam(defaultValue = "1") Integer current,
                               @RequestParam(defaultValue = "10") Integer size,
                               @RequestParam(required = false) Long categoryId) {
        try {
            Page<Article> page = new Page<>(current, size);
            Page<ArticleVo> articlePage = articleService.listArticles(page, categoryId);
            return Result.success(articlePage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询当前用户的文章列表
     *
     * @param current    当前页码
     * @param size       每页大小
     * @param categoryId 分类ID
     * @return 文章列表
     */
    @GetMapping("/my")
    public Result listMyArticles(@RequestParam(defaultValue = "1") Integer current,
                                @RequestParam(defaultValue = "10") Integer size,
                                @RequestParam(required = false) Long categoryId) {
        try {
            Page<Article> page = new Page<>(current, size);
            Page<ArticleVo> articlePage = articleService.listMyArticles(page, categoryId);
            return Result.success(articlePage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询待审核的文章列表（管理员使用）
     *
     * @param current    当前页码
     * @param size       每页大小
     * @return 待审核的文章列表
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result listPendingArticles(@RequestParam(defaultValue = "1") Integer current,
                                     @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<Article> page = new Page<>(current, size);
            Page<ArticleVo> articlePage = articleService.listPendingArticles(page);
            return Result.success(articlePage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新文章（需要管理员权限或文章作者）
     *
     * @param id 文章ID
     * @param article 文章信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result updateArticle(@PathVariable Long id, @RequestBody Article article) {
        try {
            // 获取文章信息
            ArticleVo existingArticle = articleService.getArticleById(id);
            if (existingArticle == null) {
                return Result.error("文章不存在");
            }
            
            // 获取当前用户ID
            Long currentUserId = SecurityUtils.getCurrentUserId();
            // 判断是否是管理员或文章作者
            boolean isAdmin = SecurityUtils.isAdmin();
            
            if (isAdmin || existingArticle.getUserId().equals(currentUserId)) {
                // 设置文章ID和用户ID
                article.setId(id);
                article.setUserId(currentUserId);
                
                // 如果不是管理员，更新后的文章需要重新审核
                if (!isAdmin) {
                    article.setStatus(2); // 设置为待审核状态
                }
                
                boolean result = articleService.updateArticle(article);
                if (result) {
                    return Result.success("更新成功" + (isAdmin ? "" : "，等待审核"));
                } else {
                    return Result.error("更新失败");
                }
            } else {
                return Result.error("无权限更新该文章");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文章（需要管理员权限或文章作者）
     *
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result deleteArticle(@PathVariable Long id) {
        try {
            // 获取文章信息
            ArticleVo article = articleService.getArticleById(id);
            if (article == null) {
                return Result.error("文章不存在");
            }
            
            // 获取当前用户ID
            Long currentUserId = SecurityUtils.getCurrentUserId();
            // 判断是否是管理员或文章作者
            boolean isAdmin = SecurityUtils.isAdmin();
            
            if (isAdmin || article.getUserId().equals(currentUserId)) {
                boolean result = articleService.deleteArticle(id);
                if (result) {
                    return Result.success("删除成功");
                } else {
                    return Result.error("删除失败");
                }
            } else {
                return Result.error("无权限删除该文章");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核通过文章（需要管理员权限）
     *
     * @param id 文章ID
     * @return 审核结果
     */
    @PutMapping("/approve/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result approveArticle(@PathVariable Long id) {
        try {
            boolean result = articleService.approveArticle(id);
            if (result) {
                return Result.success("审核通过");
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核拒绝文章（需要管理员权限）
     *
     * @param id 文章ID
     * @return 审核结果
     */
    @PutMapping("/reject/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result rejectArticle(@PathVariable Long id) {
        try {
            boolean result = articleService.rejectArticle(id);
            if (result) {
                return Result.success("审核拒绝");
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 点赞或取消点赞文章
     *
     * @param id 文章ID
     * @return 点赞结果
     */
    @PostMapping("/{id}/like")
    public Result toggleLikeArticle(@PathVariable Long id) {
        try {
            // 检查用户是否登录
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                return Result.error("请先登录");
            }
            
            // 检查文章是否存在
            ArticleVo article = articleService.getArticleById(id);
            if (article == null) {
                return Result.error("文章不存在");
            }
            
            // 点赞或取消点赞
            final boolean isLiked = articleService.toggleLike(id);
            
            // 获取最新的文章信息
            final ArticleVo updatedArticle = articleService.getArticleById(id);
            
            // 返回操作结果和最新点赞状态
            return Result.success(new HashMap<String, Object>() {{
                put("message", isLiked ? "点赞成功" : "取消点赞成功");
                put("isLiked", isLiked);
                put("likeCount", updatedArticle.getLikeCount());
            }});
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查用户是否已点赞文章
     *
     * @param id 文章ID
     * @return 是否已点赞
     */
    @GetMapping("/{id}/like/status")
    public Result checkLikeStatus(@PathVariable Long id) {
        try {
            // 检查用户是否登录
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                return Result.success(false);
            }
            
            // 检查是否已点赞
            boolean hasLiked = articleService.hasLiked(id);
            return Result.success(hasLiked);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
