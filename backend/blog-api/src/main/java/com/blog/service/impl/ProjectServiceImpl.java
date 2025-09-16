package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Project;
import com.blog.mapper.ProjectMapper;
import com.blog.service.ProjectLikeService;
import com.blog.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 开发项目服务实现类
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectLikeService projectLikeService;

    @Override
    public List<Project> getAllProjects() {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort_order", "create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Project> getAllPublicProjects() {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_public", true);
        queryWrapper.orderByDesc("is_featured", "sort_order", "create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Project> getFeaturedProjects() {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_public", true);
        queryWrapper.eq("is_featured", true);
        queryWrapper.orderByDesc("sort_order", "create_time");
        return this.list(queryWrapper);
    }

    @Override
    public List<Project> getProjectsByCategory(Long categoryId) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_public", true);
        queryWrapper.eq("category_id", categoryId);
        queryWrapper.orderByDesc("sort_order", "create_time");
        return this.list(queryWrapper);
    }

    @Override
    public boolean incrementViewCount(Long projectId) {
        Project project = this.getById(projectId);
        if (project != null) {
            project.setViewCount(project.getViewCount() + 1);
            return this.updateById(project);
        }
        return false;
    }

    @Override
    public boolean likeProject(Long projectId, Long userId, String ipAddress) {
        Project project = this.getById(projectId);
        if (project != null) {
            project.setLikeCount(project.getLikeCount() + 1);
            return this.updateById(project);
        }
        return false;
    }

    @Override
    public java.util.Map<String, Object> toggleLike(Long projectId, Long userId, String ipAddress) {
        return projectLikeService.toggleProjectLike(projectId, userId, ipAddress);
    }

    @Override
    public boolean isProjectLiked(Long projectId, Long userId, String ipAddress) {
        return projectLikeService.isProjectLiked(projectId, userId, ipAddress);
    }
    
    @Override
    public boolean addProject(Project project) {
        return this.save(project);
    }
    
    @Override
    public boolean updateProject(Project project) {
        return this.updateById(project);
    }
    
    @Override
    public boolean deleteProject(Long id) {
        return this.removeById(id);
    }
}