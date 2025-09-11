package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 个人基本信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_about_me")
public class AboutMe {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 职位/头衔
     */
    private String title;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String introduction;

    /**
     * 所在地
     */
    private String location;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * GitHub地址
     */
    private String github;

    /**
     * LinkedIn地址
     */
    private String linkedin;

    /**
     * 个人网站
     */
    private String website;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * QQ号
     */
    private String qq;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 状态 0:禁用 1:启用
     */
    private Integer status;

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
}