package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.InternshipExperience;

import java.util.List;

/**
 * 实习经历服务接口
 */
public interface InternshipExperienceService extends IService<InternshipExperience> {
    
    /**
     * 获取所有实习经历
     */
    List<InternshipExperience> getAllInternshipExperience();
    
    /**
     * 添加实习经历
     */
    boolean addInternshipExperience(InternshipExperience internshipExperience);
    
    /**
     * 更新实习经历
     */
    boolean updateInternshipExperience(InternshipExperience internshipExperience);
    
    /**
     * 删除实习经历
     */
    boolean deleteInternshipExperience(Long id);
}