package com.blog.vo;

import com.blog.entity.Article;
import com.blog.entity.User;
import lombok.Data;

import java.util.Date;

/**
 * 文章视图对象，包含文章和作者信息
 */
@Data
public class ArticleVo {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章封面
     */
    private String cover;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 作者ID
     */
    private Long userId;

    /**
     * 作者名称
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String authorAvatar;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否置顶 0:否 1:是
     */
    private Integer isTop;

    /**
     * 状态 0:草稿 1:已发布 2:待审核 3:审核不通过
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 从文章实体和用户实体构建文章视图对象
     *
     * @param article 文章实体
     * @param user 用户实体
     * @return 文章视图对象
     */
    public static ArticleVo fromArticleAndUser(Article article, User user) {
        ArticleVo vo = new ArticleVo();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setSummary(article.getSummary());
        vo.setContent(article.getContent());
        vo.setCover(article.getCover());
        vo.setCategoryId(article.getCategoryId());
        vo.setUserId(article.getUserId());
        vo.setViewCount(article.getViewCount());
        vo.setCommentCount(article.getCommentCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setIsTop(article.getIsTop());
        vo.setStatus(article.getStatus());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());
        
        if (user != null) {
            vo.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            vo.setAuthorAvatar(user.getAvatar());
        }
        
        return vo;
    }
}