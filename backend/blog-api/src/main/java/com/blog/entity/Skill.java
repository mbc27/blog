package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 技能实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_skill")
public class Skill {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 技能名称
     */
    private String name;

    /**
     * 技能分类ID
     */
    private Long categoryId;

    /**
     * 熟练度(0-100)
     */
    private Integer proficiency;

    /**
     * 使用年限
     */
    private BigDecimal yearsExperience;

    /**
     * 技能描述
     */
    private String description;

    /**
     * 技能图标
     */
    private String icon;

    /**
     * 技能颜色
     */
    private String color;

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