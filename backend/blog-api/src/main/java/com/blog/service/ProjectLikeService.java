package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.ProjectLike;

import java.util.Map;

/**
 * 项目点赞服务接口
 */
public interface ProjectLikeService extends IService<ProjectLike> {
    
    /**
     * 切换项目点赞状态
     * @param projectId 项目ID
     * @param userId 用户ID（可为空）
     * @param ipAddress IP地址
     * @return 点赞结果
     */
    Map<String, Object> toggleProjectLike(Long projectId, Long userId, String ipAddress);
    
    /**
     * 检查用户是否已点赞项目
     * @param projectId 项目ID
     * @param userId 用户ID（可为空）
     * @param ipAddress IP地址
     * @return 是否已点赞
     */
    boolean isProjectLiked(Long projectId, Long userId, String ipAddress);
}