package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ProjectCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目分类Mapper接口
 */
@Mapper
public interface ProjectCategoryMapper extends BaseMapper<ProjectCategory> {
}