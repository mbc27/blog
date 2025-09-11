package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 开发项目实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_project")
public class Project {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目标题
     */
    private String title;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 项目简介
     */
    private String summary;

    /**
     * 项目分类ID
     */
    private Long categoryId;

    /**
     * 项目封面图
     */
    private String coverImage;

    /**
     * 项目截图(JSON数组)
     */
    private String images;

    /**
     * 使用技术栈
     */
    private String technologies;

    /**
     * 开始时间
     */
    private LocalDate startDate;

    /**
     * 结束时间
     */
    private LocalDate endDate;

    /**
     * 项目状态(planning/developing/completed/maintenance)
     */
    private String status;

    /**
     * 项目类型(personal/work/school/open-source)
     */
    private String projectType;

    /**
     * 团队规模
     */
    private Integer teamSize;

    /**
     * 我的角色
     */
    private String myRole;

    /**
     * GitHub地址
     */
    private String githubUrl;

    /**
     * 演示地址
     */
    private String demoUrl;

    /**
     * 下载地址
     */
    private String downloadUrl;

    /**
     * 文档地址
     */
    private String documentationUrl;

    /**
     * 主要功能特性
     */
    private String features;

    /**
     * 技术难点/挑战
     */
    private String challenges;

    /**
     * 项目成果/收获
     */
    private String achievements;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否精选项目
     */
    private Boolean isFeatured;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除 0:未删除 1:已删除
     */
    @TableLogic
    private Integer deleted;
}