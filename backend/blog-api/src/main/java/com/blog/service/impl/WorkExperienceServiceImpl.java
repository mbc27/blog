package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.WorkExperience;
import com.blog.mapper.WorkExperienceMapper;
import com.blog.service.WorkExperienceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 工作经历服务实现类
 */
@Service
public class WorkExperienceServiceImpl extends ServiceImpl<WorkExperienceMapper, WorkExperience> implements WorkExperienceService {

    @Override
    public List<WorkExperience> getAllWorkExperience() {
        QueryWrapper<WorkExperience> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort_order", "start_date");
        return this.list(queryWrapper);
    }
    
    @Override
    public boolean addWorkExperience(WorkExperience workExperience) {
        return this.save(workExperience);
    }
    
    @Override
    public boolean updateWorkExperience(WorkExperience workExperience) {
        return this.updateById(workExperience);
    }
    
    @Override
    public boolean deleteWorkExperience(Long id) {
        return this.removeById(id);
    }
}