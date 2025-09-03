package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.PhotoCategory;

import java.util.List;

/**
 * 相册分类服务接口
 */
public interface PhotoCategoryService extends IService<PhotoCategory> {

    /**
     * 获取所有相册分类列表
     *
     * @return 相册分类列表
     */
    List<PhotoCategory> listAllCategories();

    /**
     * 根据ID获取相册分类
     *
     * @param id 相册分类ID
     * @return 相册分类信息
     */
    PhotoCategory getCategoryById(Long id);

    /**
     * 新增相册分类
     *
     * @param category 相册分类信息
     * @return 是否成功
     */
    boolean addCategory(PhotoCategory category);

    /**
     * 更新相册分类
     *
     * @param category 相册分类信息
     * @return 是否成功
     */
    boolean updateCategory(PhotoCategory category);

    /**
     * 删除相册分类
     *
     * @param id 相册分类ID
     * @return 是否成功
     */
    boolean deleteCategory(Long id);
}