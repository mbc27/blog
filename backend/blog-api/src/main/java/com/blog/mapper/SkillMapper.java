package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Skill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 技能Mapper接口
 */
@Mapper
public interface SkillMapper extends BaseMapper<Skill> {
}