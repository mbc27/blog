package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 开发项目Mapper接口
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}