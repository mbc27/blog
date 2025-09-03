package com.blog.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import com.blog.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    @GetMapping("/list")
    public Result listAllTags() {
        try {
            List<Tag> tags = tagService.listAllTags();
            return Result.success(tags);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分页获取标签列表（管理员用）
     *
     * @param current 当前页
     * @param size 页大小
     * @return 分页标签列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result listTagsWithPagination(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<Tag> page = new Page<>(current, size);
            Page<Tag> tagPage = tagService.listTagsWithPagination(page);
            return Result.success(tagPage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取标签详情
     *
     * @param id 标签ID
     * @return 标签详情
     */
    @GetMapping("/{id}")
    public Result getTag(@PathVariable Long id) {
        try {
            Tag tag = tagService.getTagById(id);
            if (tag == null) {
                return Result.error("标签不存在");
            }
            return Result.success(tag);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据文章ID获取标签列表
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    @GetMapping("/article/{articleId}")
    public Result getTagsByArticleId(@PathVariable Long articleId) {
        try {
            List<Tag> tags = tagService.listTagsByArticleId(articleId);
            return Result.success(tags);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增标签（需要管理员权限）
     *
     * @param tag 标签信息
     * @return 新增结果
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result addTag(@RequestBody Tag tag) {
        try {
            boolean result = tagService.addTag(tag);
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
     * 更新标签（需要管理员权限）
     *
     * @param id 标签ID
     * @param tag 标签信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        try {
            tag.setId(id);
            boolean result = tagService.updateTag(tag);
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
     * 删除标签（需要管理员权限）
     *
     * @param id 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result deleteTag(@PathVariable Long id) {
        try {
            boolean result = tagService.deleteTag(id);
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