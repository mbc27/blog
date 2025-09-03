package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 相册实体类
 */
@Data
@TableName("tb_photo")
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 相册ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 相册标题
     */
    @TableField("title")
    private String title;

    /**
     * 相册描述
     */
    @TableField("description")
    private String description;

    /**
     * 图片地址
     */
    @TableField("url")
    private String url;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 浏览量
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}
