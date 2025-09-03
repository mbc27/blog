package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章分类控制器
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类
     *
     * @return 分类列表
     */
    @GetMapping("/list")
    public Result listAllCategories() {
        try {
            List<Category> categories = categoryService.listAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页获取分类列表（管理员用）
     *
     * @param current 当前页
     * @param size 页大小
     * @return 分页分类列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result listCategoriesWithPagination(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<Category> page = new Page<>(current, size);
            Page<Category> categoryPage = categoryService.listCategoriesWithPagination(page);
            return Result.success(categoryPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    @GetMapping("/{id}")
    public Result getCategory(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            if (category == null) {
                return Result.error("分类不存在");
            }
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增分类（需要管理员权限）
     *
     * @param category 分类信息
     * @return 新增结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result addCategory(@RequestBody Category category) {
        try {
            boolean result = categoryService.addCategory(category);
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
     * 更新分类（需要管理员权限）
     *
     * @param category 分类信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            boolean result = categoryService.updateCategory(category);
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
     * 删除分类（需要管理员权限）
     *
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result deleteCategory(@PathVariable Long id) {
        try {
            boolean result = categoryService.deleteCategory(id);
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