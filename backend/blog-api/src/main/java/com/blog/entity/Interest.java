package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 个人兴趣实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_interest")
public class Interest {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 兴趣名称
     */
    private String name;

    /**
     * 兴趣描述
     */
    private String description;

    /**
     * 兴趣图标
     */
    private String icon;

    /**
     * 颜色
     */
    private String color;

    /**
     * 兴趣程度(初级/中级/高级/专业)
     */
    private String level;

    /**
     * 接触年限
     */
    private BigDecimal yearsExperience;

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