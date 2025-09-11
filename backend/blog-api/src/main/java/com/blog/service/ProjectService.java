package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Project;

import java.util.List;

/**
 * 开发项目服务接口
 */
public interface ProjectService extends IService<Project> {
    
    /**
     * 获取所有项目（管理员用）
     */
    List<Project> getAllProjects();
    
    /**
     * 获取所有公开项目
     */
    List<Project> getAllPublicProjects();
    
    /**
     * 获取精选项目
     */
    List<Project> getFeaturedProjects();
    
    /**
     * 根据分类获取项目
     */
    List<Project> getProjectsByCategory(Long categoryId);
    
    /**
     * 增加项目浏览量
     */
    void incrementViewCount(Long projectId);
    
    /**
     * 项目点赞
     */
    boolean likeProject(Long projectId, Long userId, String ipAddress);
    
    /**
     * 添加项目
     */
    boolean addProject(Project project);
    
    /**
     * 更新项目
     */
    boolean updateProject(Project project);
    
    /**
     * 删除项目
     */
    boolean deleteProject(Long id);
}