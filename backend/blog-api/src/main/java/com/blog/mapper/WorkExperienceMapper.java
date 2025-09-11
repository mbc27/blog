package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.WorkExperience;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作经历Mapper接口
 */
@Mapper
public interface WorkExperienceMapper extends BaseMapper<WorkExperience> {
}