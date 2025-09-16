package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ProjectLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 项目点赞记录Mapper接口
 */
@Mapper
public interface ProjectLikeMapper extends BaseMapper<ProjectLike> {
    
    /**
     * 根据项目ID和用户ID查找点赞记录
     */
    ProjectLike findByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);
    
    /**
     * 根据项目ID和IP地址查找点赞记录
     */
    ProjectLike findByProjectIdAndIpAddress(@Param("projectId") Long projectId, @Param("ipAddress") String ipAddress);
}