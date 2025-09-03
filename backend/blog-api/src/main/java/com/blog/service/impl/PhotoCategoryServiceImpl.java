package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dao.PhotoCategoryMapper;
import com.blog.dao.PhotoMapper;
import com.blog.entity.Photo;
import com.blog.entity.PhotoCategory;
import com.blog.service.PhotoCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 相册分类服务实现类
 */
@Service
public class PhotoCategoryServiceImpl extends ServiceImpl<PhotoCategoryMapper, PhotoCategory> implements PhotoCategoryService {

    @Autowired
    private PhotoMapper photoMapper;

    @Override
    public List<PhotoCategory> listAllCategories() {
        return this.list();
    }

    @Override
    public PhotoCategory getCategoryById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean addCategory(PhotoCategory category) {
        // 设置创建时间和更新时间
        Date now = new Date();
        category.setCreateTime(now);
        category.setUpdateTime(now);
        return this.save(category);
    }

    @Override
    public boolean updateCategory(PhotoCategory category) {
        // 设置更新时间
        category.setUpdateTime(new Date());
        return this.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        // 删除分类前，先删除该分类下的所有相册
        LambdaQueryWrapper<Photo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Photo::getCategoryId, id);
        photoMapper.delete(wrapper);
        
        // 删除分类
        return this.removeById(id);
    }
}