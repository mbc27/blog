package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章实体类
 */
@Data
@TableName("tb_article")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
     * 文章状态常量
     */
    public static final int STATUS_DRAFT = 0;        // 草稿
    public static final int STATUS_PUBLISHED = 1;    // 已发布
    public static final int STATUS_PENDING = 2;      // 待审核
    public static final int STATUS_REJECTED = 3;     // 审核不通过

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}