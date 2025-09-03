package com.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 相册数据访问接口
 */
@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {
    
    /**
     * 分页查询相册列表
     */
    @Select("SELECT id, title, description, url, category_id, view_count, create_time, update_time FROM tb_photo ORDER BY create_time DESC LIMIT #{limit}")
    List<Photo> selectPageWithFields(@Param("limit") long limit);
    
    /**
     * 根据分类ID分页查询相册列表
     */
    @Select("SELECT id, title, description, url, category_id, view_count, create_time, update_time FROM tb_photo WHERE category_id = #{categoryId} ORDER BY create_time DESC LIMIT #{limit}")
    List<Photo> selectPageByCategoryWithFields(@Param("categoryId") Long categoryId, @Param("limit") long limit);
}
