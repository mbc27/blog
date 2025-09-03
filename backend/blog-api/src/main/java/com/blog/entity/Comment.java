package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 评论实体类
 */
@Data
@TableName("tb_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父评论ID
     */
    private Long parentId;

    /**
     * 回复用户ID
     */
    private Long toUserId;

    /**
     * 评论层级
     */
    private Integer level;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
    /**
     * 用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;
    
    /**
     * 文章标题（非数据库字段）
     */
    @TableField(exist = false)
    private String articleTitle;
    
    /**
     * 用户昵称（非数据库字段）
     */
    @TableField(exist = false)
    private String nickname;
    
    /**
     * 子评论列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Comment> children;
}