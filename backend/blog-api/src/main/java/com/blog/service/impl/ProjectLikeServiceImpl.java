package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Project;
import com.blog.entity.ProjectLike;
import com.blog.mapper.ProjectLikeMapper;
import com.blog.mapper.ProjectMapper;
import com.blog.service.ProjectLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 项目点赞服务实现类
 */
@Service
public class ProjectLikeServiceImpl extends ServiceImpl<ProjectLikeMapper, ProjectLike> implements ProjectLikeService {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    @Transactional
    public Map<String, Object> toggleProjectLike(Long projectId, Long userId, String ipAddress) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查项目是否存在
            Project project = projectMapper.selectById(projectId);
            if (project == null) {
                result.put("success", false);
                result.put("message", "项目不存在");
                return result;
            }
            
            // 查找现有的点赞记录
            ProjectLike existingLike = findExistingLike(projectId, userId, ipAddress);
            
            boolean isLiked = false;
            
            if (existingLike == null) {
                // 没有点赞记录，创建新的点赞记录
                ProjectLike newLike = new ProjectLike();
                newLike.setProjectId(projectId);
                newLike.setUserId(userId);
                newLike.setIpAddress(ipAddress);
                newLike.setCreateTime(LocalDateTime.now());
                
                this.save(newLike);
                
                // 增加项目点赞数
                project.setLikeCount(project.getLikeCount() + 1);
                isLiked = true;
                result.put("message", "点赞成功");
                
            } else {
                // 已经点赞，取消点赞（删除记录）
                this.removeById(existingLike.getId());
                
                // 减少项目点赞数
                project.setLikeCount(Math.max(project.getLikeCount() - 1, 0));
                isLiked = false;
                result.put("message", "取消点赞");
            }
            
            // 更新项目点赞数
            projectMapper.updateById(project);
            
            result.put("success", true);
            result.put("liked", isLiked);
            result.put("likeCount", project.getLikeCount());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    public boolean isProjectLiked(Long projectId, Long userId, String ipAddress) {
        ProjectLike existingLike = findExistingLike(projectId, userId, ipAddress);
        return existingLike != null;
    }
    
    /**
     * 查找现有的点赞记录
     */
    private ProjectLike findExistingLike(Long projectId, Long userId, String ipAddress) {
        QueryWrapper<ProjectLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", projectId);
        
        if (userId != null) {
            // 登录用户，按用户ID查找
            queryWrapper.eq("user_id", userId);
        } else {
            // 游客，按IP地址查找
            queryWrapper.eq("ip_address", ipAddress);
            queryWrapper.isNull("user_id");
        }
        
        return this.getOne(queryWrapper);
    }
}