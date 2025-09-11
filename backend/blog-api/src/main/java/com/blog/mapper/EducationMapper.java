package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Education;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教育背景Mapper接口
 */
@Mapper
public interface EducationMapper extends BaseMapper<Education> {
}