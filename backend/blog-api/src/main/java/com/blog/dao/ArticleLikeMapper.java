package com.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ArticleLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章点赞记录Mapper接口
 */
@Mapper
public interface ArticleLikeMapper extends BaseMapper<ArticleLike> {
}