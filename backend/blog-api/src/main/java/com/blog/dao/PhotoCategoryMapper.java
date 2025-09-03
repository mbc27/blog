package com.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.PhotoCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 相册分类数据访问接口
 */
@Mapper
public interface PhotoCategoryMapper extends BaseMapper<PhotoCategory> {
    
    @Select("SELECT id, name, description, create_time, update_time FROM tb_photo_category ORDER BY create_time DESC")
    Page<PhotoCategory> selectPageWithFields(Page<PhotoCategory> page);
}
