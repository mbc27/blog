package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.WorkExperience;

import java.util.List;

/**
 * 工作经历服务接口
 */
public interface WorkExperienceService extends IService<WorkExperience> {
    
    /**
     * 获取所有工作经历
     */
    List<WorkExperience> getAllWorkExperience();
    
    /**
     * 添加工作经历
     */
    boolean addWorkExperience(WorkExperience workExperience);
    
    /**
     * 更新工作经历
     */
    boolean updateWorkExperience(WorkExperience workExperience);
    
    /**
     * 删除工作经历
     */
    boolean deleteWorkExperience(Long id);
}