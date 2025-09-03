package com.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章标签关联数据访问接口
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}