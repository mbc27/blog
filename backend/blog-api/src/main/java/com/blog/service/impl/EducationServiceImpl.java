package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Education;
import com.blog.mapper.EducationMapper;
import com.blog.service.EducationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教育背景服务实现类
 */
@Service
public class EducationServiceImpl extends ServiceImpl<EducationMapper, Education> implements EducationService {

    @Override
    public List<Education> getAllEducation() {
        QueryWrapper<Education> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("sort_order", "start_date");
        return this.list(queryWrapper);
    }
    
    @Override
    public boolean addEducation(Education education) {
        return this.save(education);
    }
    
    @Override
    public boolean updateEducation(Education education) {
        return this.updateById(education);
    }
    
    @Override
    public boolean deleteEducation(Long id) {
        return this.removeById(id);
    }
}