package com.blog.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户VO类
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 状态 0:禁用，1:正常
     */
    private Integer status;

    /**
     * 角色 0:管理员，1:普通用户
     */
    private Integer role;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * JWT Token
     */
    private String token;
}