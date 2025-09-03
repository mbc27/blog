package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 留言实体类
 */
@Data
@TableName("tb_message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 留言ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 留言人昵称
     */
    private String nickname;

    /**
     * 留言人邮箱
     */
    private String email;

    /**
     * 留言人头像
     */
    private String avatar;

    /**
     * 父留言ID
     */
    private Long parentId;

    /**
     * 回复用户昵称
     */
    private String toNickname;

    /**
     * 留言层级
     */
    private Integer level;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}