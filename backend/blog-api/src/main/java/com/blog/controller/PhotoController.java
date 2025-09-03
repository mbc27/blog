package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Photo;
import com.blog.entity.PhotoCategory;
import com.blog.service.PhotoCategoryService;
import com.blog.service.PhotoService;

import java.util.List;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 相册控制器
 */
@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;
    
    @Autowired
    private PhotoCategoryService photoCategoryService;

    /**
     * 分页查询相册列表
     *
     * @param current    当前页
     * @param size       每页大小
     * @param categoryId 分类ID
     * @return 相册列表
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer current,
                       @RequestParam(defaultValue = "10") Integer size,
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
     * 管理员分页查询相册列表
     *
     * @param current    当前页
     * @param size       每页大小
     * @param categoryId 分类ID
     * @return 相册列表
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result adminList(@RequestParam(defaultValue = "1") Integer current,
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
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result getCategories() {
        try {
            List<PhotoCategory> categories = photoCategoryService.listAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取相册详情
     *
     * @param id 相册ID
     * @return 相册详情
     */
    @GetMapping("/{id}")
    public Result getPhoto(@PathVariable Long id) {
        try {
            Photo photo = photoService.getPhotoById(id);
            if (photo == null) {
                return Result.error("相册不存在");
            }
            // 增加浏览量
            photoService.incrementViewCount(id);
            return Result.success(photo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增相册（需要管理员权限）
     *
     * @param photo 相册信息
     * @return 新增结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result addPhoto(@RequestBody Photo photo) {
        try {
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
     * 更新相册（需要管理员权限）
     *
     * @param photo 相册信息
     * @return 更新结果
     */
    @PutMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result updatePhoto(@RequestBody Photo photo) {
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
     * 删除相册（需要管理员权限）
     *
     * @param id 相册ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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