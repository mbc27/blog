package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Photo;
import com.blog.entity.PhotoCategory;
import com.blog.service.PhotoService;
import com.blog.service.PhotoCategoryService;
import com.blog.dao.PhotoCategoryMapper;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员相册控制器
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminPhotoController {

    @Autowired
    private PhotoService photoService;
    
    @Autowired
    private PhotoCategoryService photoCategoryService;
    
    @Autowired
    private PhotoCategoryMapper photoCategoryMapper;

    /**
     * 管理员分页查询相册列表
     *
     * @param current    当前页
     * @param size       每页大小
     * @param categoryId 分类ID
     * @return 相册列表
     */
    @GetMapping("/photos")
    public Result listPhotos(@RequestParam(defaultValue = "1") Integer current,
                             @RequestParam(defaultValue = "12") Integer size,
                             @RequestParam(required = false) Long categoryId) {
        try {
            Page<Photo> page = new Page<>(current, size);
            Page<Photo> photoPage = photoService.listPhotos(page, categoryId);
            return Result.success(photoPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取相册分类列表
     *
     * @param current 当前页
     * @param size    每页大小
     * @return 分类列表
     */
    @GetMapping("/photo/categories")
    public Result getPhotoCategories(@RequestParam(defaultValue = "1") Integer current,
                                    @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<PhotoCategory> page = new Page<>(current, size);
            Page<PhotoCategory> categoryPage = photoCategoryMapper.selectPageWithFields(page);
            return Result.success(categoryPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 添加相册分类
     *
     * @param category 分类信息
     * @return 添加结果
     */
    @PostMapping("/photo/categories")
    public Result addPhotoCategory(@RequestBody PhotoCategory category) {
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
     * 更新相册分类
     *
     * @param id 分类ID
     * @param category 分类信息
     * @return 更新结果
     */
    @PutMapping("/photo/categories/{id}")
    public Result updatePhotoCategory(@PathVariable Long id, @RequestBody PhotoCategory category) {
        try {
            category.setId(id);
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
     * 删除相册分类
     *
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/photo/categories/{id}")
    public Result deletePhotoCategory(@PathVariable Long id) {
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

    /**
     * 新增相册
     *
     * @param photo 相册信息
     * @return 新增结果
     */
    @PostMapping("/photos")
    public Result addPhoto(@RequestBody Photo photo) {
        try {
            // 如果URL是相对路径，转换为完整的服务器地址
            String url = photo.getUrl();
            if (url != null && url.startsWith("/uploads/") && !url.startsWith("http")) {
                photo.setUrl("http://localhost:8080" + url);
            }
            
            boolean result = photoService.addPhoto(photo);
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
     * 更新相册
     *
     * @param photo 相册信息
     * @return 更新结果
     */
    @PutMapping("/photos/{id}")
    public Result updatePhoto(@PathVariable Long id, @RequestBody Photo photo) {
        // 确保设置正确的ID
        photo.setId(id);
        try {
            boolean result = photoService.updatePhoto(photo);
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
     * 删除相册
     *
     * @param id 相册ID
     * @return 删除结果
     */
    @DeleteMapping("/photos/{id}")
    public Result deletePhoto(@PathVariable Long id) {
        try {
            boolean result = photoService.deletePhoto(id);
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