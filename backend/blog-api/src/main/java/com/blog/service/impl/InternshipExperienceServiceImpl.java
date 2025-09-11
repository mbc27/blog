package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.InternshipExperience;
import com.blog.mapper.InternshipExperienceMapper;
import com.blog.service.InternshipExperienceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实习经历服务实现类
 */
@Service
public class InternshipExperienceServiceImpl extends ServiceImpl<InternshipExperienceMapper, InternshipExperience> implements InternshipExperienceService {

    @Override
    public List<InternshipExperience> getAllInternshipExperience() {
        QueryWrapper<InternshipExperience> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort_order", "start_date");
        return this.list(queryWrapper);
    }
    
    @Override
    public boolean addInternshipExperience(InternshipExperience internshipExperience) {
        return this.save(internshipExperience);
    }
    
    @Override
    public boolean updateInternshipExperience(InternshipExperience internshipExperience) {
        return this.updateById(internshipExperience);
    }
    
    @Override
    public boolean deleteInternshipExperience(Long id) {
        return this.removeById(id);
    }
}