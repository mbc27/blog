package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 项目点赞记录实体类
 */
@Data
@TableName("tb_project_like")
public class ProjectLike {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 项目ID
     */
    @TableField("project_id")
    private Long projectId;
    
    /**
     * 用户ID（可为空，游客点赞时为空）
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * IP地址（用于游客点赞识别）
     */
    @TableField("ip_address")
    private String ipAddress;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}