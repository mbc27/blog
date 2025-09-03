package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 文章点赞记录实体类
 */
@Data
@TableName("tb_article_like")
public class ArticleLike {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long articleId;
    
    private Long userId;
    
    private Date createTime;
}