package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Category;

import java.util.List;

/**
 * 文章分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取所有分类列表
     *
     * @return 分类列表
     */
    List<Category> listAllCategories();

    /**
     * 分页获取分类列表
     *
     * @param page 分页参数
     * @return 分页分类列表
     */
    Page<Category> listCategoriesWithPagination(Page<Category> page);

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    Category getCategoryById(Long id);

    /**
     * 新增分类
     *
     * @param category 分类信息
     * @return 是否成功
     */
    boolean addCategory(Category category);

    /**
     * 更新分类
     *
     * @param category 分类信息
     * @return 是否成功
     */
    boolean updateCategory(Category category);

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    boolean deleteCategory(Long id);
}