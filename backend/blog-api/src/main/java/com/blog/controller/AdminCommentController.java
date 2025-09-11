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
 * 管理员评论控制器
 */
@RestController
@RequestMapping("/api/admin/comments")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

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
     * 更新评论状态
     *
     * @param id     评论ID
     * @param status 状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public Result updateCommentStatus(@PathVariable Long id, @RequestBody StatusRequest status) {
        try {
            Comment comment = commentService.getCommentById(id);
            if (comment == null) {
                return Result.error("评论不存在");
            }
            
            // 这里可以根据需要更新评论状态
            // 由于Comment实体暂时没有status字段，这里只是示例
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
     * 删除评论
     *
     * @param id 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
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
     * 审核通过评论
     *
     * @param id 评论ID
     * @return 审核结果
     */
    @PutMapping("/{id}/approve")
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
     * 获取评论回复列表
     *
     * @param id 评论ID
     * @return 回复列表
     */
    @GetMapping("/{id}/replies")
    public Result getCommentReplies(@PathVariable Long id) {
        try {
            List<Comment> replies = commentService.getCommentReplies(id);
            return Result.success(replies);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 状态请求类
     */
    public static class StatusRequest {
        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}