package com.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章分类数据访问接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}