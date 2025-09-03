package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {

    /**
     * 分页查询文章评论
     *
     * @param page      分页参数
     * @param articleId 文章ID
     * @return 评论分页列表
     */
    Page<Comment> listCommentsByArticleId(Page<Comment> page, Long articleId);

    /**
     * 获取评论详情
     *
     * @param id 评论ID
     * @return 评论信息
     */
    Comment getCommentById(Long id);

    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 是否成功
     */
    boolean addComment(Comment comment);

    /**
     * 更新评论
     *
     * @param comment 评论信息
     * @return 是否成功
     */
    boolean updateComment(Comment comment);

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return 是否成功
     */
    boolean deleteComment(Long id);

    /**
     * 获取文章评论树
     *
     * @param articleId 文章ID
     * @return 评论树列表
     */
    List<Comment> getCommentTree(Long articleId);

    /**
     * 管理员分页查询所有评论
     *
     * @param page         分页参数
     * @param articleTitle 文章标题（可选）
     * @param content      评论内容（可选）
     * @param status       评论状态（可选）
     * @return 评论分页列表
     */
    Page<Comment> listAllComments(Page<Comment> page, String articleTitle, String content, Integer status);

    /**
     * 审核通过评论
     *
     * @param id 评论ID
     * @return 是否成功
     */
    boolean approveComment(Long id);

    /**
     * 获取评论回复列表
     *
     * @param id 评论ID
     * @return 回复列表
     */
    List<Comment> getCommentReplies(Long id);

    /**
     * 获取文章评论数量
     *
     * @param articleId 文章ID
     * @return 评论数量
     */
    int getCommentCountByArticleId(Long articleId);
}
