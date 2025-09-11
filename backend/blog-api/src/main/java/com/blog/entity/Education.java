package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 教育背景实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_education")
public class Education {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学校名称
     */
    private String schoolName;

    /**
     * 学位
     */
    private String degree;

    /**
     * 专业
     */
    private String major;

    /**
     * 入学时间
     */
    private LocalDate startDate;

    /**
     * 毕业时间
     */
    private LocalDate endDate;

    /**
     * 是否在读
     */
    private Boolean isCurrent;

    /**
     * 学校地点
     */
    private String location;

    /**
     * 学校Logo
     */
    private String schoolLogo;

    /**
     * GPA成绩
     */
    private BigDecimal gpa;

    /**
     * 教育经历描述
     */
    private String description;

    /**
     * 主要成就/荣誉
     */
    private String achievements;

    /**
     * 主要课程
     */
    private String courses;

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