package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.SkillCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 技能分类Mapper接口
 */
@Mapper
public interface SkillCategoryMapper extends BaseMapper<SkillCategory> {
}