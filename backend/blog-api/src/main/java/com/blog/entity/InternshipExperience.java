package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实习经历实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_internship_experience")
public class InternshipExperience {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 实习职位
     */
    private String position;

    /**
     * 部门
     */
    private String department;

    /**
     * 开始时间
     */
    private LocalDate startDate;

    /**
     * 结束时间
     */
    private LocalDate endDate;

    /**
     * 实习地点
     */
    private String location;

    /**
     * 公司Logo
     */
    private String companyLogo;

    /**
     * 实习内容描述
     */
    private String jobDescription;

    /**
     * 实习成果
     */
    private String achievements;

    /**
     * 使用技术栈
     */
    private String technologies;

    /**
     * 指导老师/导师
     */
    private String mentor;

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