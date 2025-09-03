package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 友链实体类
 */
@Data
@TableName("tb_friend_link")
public class FriendLink {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 友链名称
     */
    private String name;
    
    /**
     * 友链描述
     */
    private String description;
    
    /**
     * 友链地址
     */
    private String url;
    
    /**
     * 友链头像
     */
    private String avatar;
    
    /**
     * 状态 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    /**
     * 是否删除 0-未删除 1-已删除
     */
    @TableLogic
    private Integer deleted;
}