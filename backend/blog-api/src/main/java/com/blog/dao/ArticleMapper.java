package com.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 文章数据访问接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    
    /**
     * 获取所有文章的访问量总和
     */
    @Select("SELECT COALESCE(SUM(view_count), 0) FROM tb_article WHERE status = 1")
    Long getTotalViewCount();
}