package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Skill;

import java.util.List;

/**
 * 技能服务接口
 */
public interface SkillService extends IService<Skill> {
    
    /**
     * 获取所有技能（包含分类信息）
     */
    List<Skill> getAllSkillsWithCategory();
    
    /**
     * 根据分类获取技能
     */
    List<Skill> getSkillsByCategory(Long categoryId);
    
    /**
     * 添加技能
     */
    boolean addSkill(Skill skill);
    
    /**
     * 更新技能
     */
    boolean updateSkill(Skill skill);
    
    /**
     * 删除技能
     */
    boolean deleteSkill(Long id);
}