package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Skill;
import com.blog.mapper.SkillMapper;
import com.blog.service.SkillService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 技能服务实现类
 */
@Service
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skill> implements SkillService {

    @Override
    public List<Skill> getAllSkillsWithCategory() {
        QueryWrapper<Skill> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("category_id", "sort_order");
        return this.list(queryWrapper);
    }

    @Override
    public List<Skill> getSkillsByCategory(Long categoryId) {
        QueryWrapper<Skill> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", categoryId);
        queryWrapper.orderByAsc("sort_order");
        return this.list(queryWrapper);
    }
    
    @Override
    public boolean addSkill(Skill skill) {
        return this.save(skill);
    }
    
    @Override
    public boolean updateSkill(Skill skill) {
        return this.updateById(skill);
    }
    
    @Override
    public boolean deleteSkill(Long id) {
        return this.removeById(id);
    }
}