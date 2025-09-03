package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 分页查询文章评论
     *
     * @param current   当前页
     * @param size      每页大小
     * @param articleId 文章ID
     * @return 评论列表
     */
    @GetMapping("/list")
    public Result listComments(@RequestParam(defaultValue = "1") Integer current,
                              @RequestParam(defaultValue = "10") Integer size,
                              @RequestParam Long articleId) {
        try {
            Page<Comment> page = new Page<>(current, size);
            Page<Comment> commentPage = commentService.listCommentsByArticleId(page, articleId);
            return Result.success(commentPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员分页查询所有评论
     *
     * @param current      当前页
     * @param size         每页大小
     * @param articleTitle 文章标题（可选）
     * @param content      评论内容（可选）
     * @param status       评论状态（可选）
     * @return 评论列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result listAllComments(@RequestParam(defaultValue = "1") Integer current,
                                  @RequestParam(defaultValue = "10") Integer size,
                                  @RequestParam(required = false) String articleTitle,
                                  @RequestParam(required = false) String content,
                                  @RequestParam(required = false) Integer status) {
        try {
            Page<Comment> page = new Page<>(current, size);
            Page<Comment> commentPage = commentService.listAllComments(page, articleTitle, content, status);
            return Result.success(commentPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评论树
     *
     * @param articleId 文章ID
     * @return 评论树
     */
    @GetMapping("/tree")
    public Result getCommentTree(@RequestParam Long articleId) {
        try {
            List<Comment> commentTree = commentService.getCommentTree(articleId);
            return Result.success(commentTree);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评论详情
     *
     * @param id 评论ID
     * @return 评论详情
     */
    @GetMapping("/{id}")
    public Result getComment(@PathVariable Long id) {
        try {
            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                return Result.error("评论不存在");
            }
            return Result.success(comment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 新增结果
     */
    @PostMapping
    public Result addComment(@RequestBody Comment comment) {
        try {
            boolean result = commentService.addComment(comment);
            if (result) {
                return Result.success("评论成功");
            } else {
                return Result.error("评论失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新评论（需要管理员权限）
     *
     * @param comment 评论信息
     * @return 更新结果
     */
    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result updateComment(@RequestBody Comment comment) {
        try {
            boolean result = commentService.updateComment(comment);
            if (result) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除评论（需要管理员权限）
     *
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result deleteComment(@PathVariable Long id) {
        try {
            boolean result = commentService.deleteComment(id);
            if (result) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 审核通过评论（需要管理员权限）
     *
     * @param id 评论ID
     * @return 审核结果
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result approveComment(@PathVariable Long id) {
        try {
            boolean result = commentService.approveComment(id);
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
     * 获取评论回复列表（需要管理员权限）
     *
     * @param id 评论ID
     * @return 回复列表
     */
    @GetMapping("/{id}/replies")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result getCommentReplies(@PathVariable Long id) {
        try {
            List<Comment> replies = commentService.getCommentReplies(id);
            return Result.success(replies);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取评论数量
     *
     * @param articleId 文章ID
     * @return 评论数量
     */
    @GetMapping("/count/{articleId}")
    public Result getCommentCount(@PathVariable Long articleId) {
        try {
            int count = commentService.getCommentCountByArticleId(articleId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}