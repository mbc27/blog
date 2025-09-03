package com.blog.controller;

import com.blog.entity.PhotoCategory;
import com.blog.service.PhotoCategoryService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 相册分类控制器
 */
@RestController
@RequestMapping("/api/photo/category")
public class PhotoCategoryController {

    @Autowired
    private PhotoCategoryService photoCategoryService;

    /**
     * 获取所有相册分类
     *
     * @return 相册分类列表
     */
    @GetMapping("/list")
    public Result listAllCategories() {
        try {
            List<PhotoCategory> categories = photoCategoryService.listAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取相册分类详情
     *
     * @param id 相册分类ID
     * @return 相册分类详情
     */
    @GetMapping("/{id}")
    public Result getCategory(@PathVariable Long id) {
        try {
            PhotoCategory category = photoCategoryService.getCategoryById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增相册分类（需要管理员权限）
     *
     * @param category 相册分类信息
     * @return 新增结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result addCategory(@RequestBody PhotoCategory category) {
        try {
            boolean result = photoCategoryService.addCategory(category);
            if (result) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新相册分类（需要管理员权限）
     *
     * @param category 相册分类信息
     * @return 更新结果
     */
    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result updateCategory(@RequestBody PhotoCategory category) {
        try {
            boolean result = photoCategoryService.updateCategory(category);
            if (result) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除相册分类（需要管理员权限）
     *
     * @param id 相册分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result deleteCategory(@PathVariable Long id) {
        try {
            boolean result = photoCategoryService.deleteCategory(id);
            if (result) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}