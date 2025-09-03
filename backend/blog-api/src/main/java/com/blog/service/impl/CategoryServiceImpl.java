package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.dao.CategoryMapper;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 文章分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listAllCategories() {
        return this.list();
    }

    @Override
    public Page<Category> listCategoriesWithPagination(Page<Category> page) {
        return this.page(page);
    }

    @Override
    public Category getCategoryById(Long id) {
        return this.getById(id);
    }

    @Override
    public boolean addCategory(Category category) {
        // 设置创建时间和更新时间
        Date now = new Date();
        category.setCreateTime(now);
        category.setUpdateTime(now);
        return this.save(category);
    }

    @Override
    public boolean updateCategory(Category category) {
        // 设置更新时间
        category.setUpdateTime(new Date());
        return this.updateById(category);
    }

    @Override
    public boolean deleteCategory(Long id) {
        return this.removeById(id);
    }
}