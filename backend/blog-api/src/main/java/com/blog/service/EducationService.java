package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Education;

import java.util.List;

/**
 * 教育背景服务接口
 */
public interface EducationService extends IService<Education> {
    
    /**
     * 获取所有教育背景
     */
    List<Education> getAllEducation();
    
    /**
     * 添加教育背景
     */
    boolean addEducation(Education education);
    
    /**
     * 更新教育背景
     */
    boolean updateEducation(Education education);
    
    /**
     * 删除教育背景
     */
    boolean deleteEducation(Long id);
}