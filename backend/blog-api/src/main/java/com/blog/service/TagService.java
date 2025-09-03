package com.blog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tag> {

    /**
     * 获取所有标签列表
     *
     * @return 标签列表
     */
    List<Tag> listAllTags();

    /**
     * 分页获取标签列表
     *
     * @param page 分页参数
     * @return 分页标签列表
     */
    Page<Tag> listTagsWithPagination(Page<Tag> page);

    /**
     * 根据ID获取标签
     *
     * @param id 标签ID
     * @return 标签信息
     */
    Tag getTagById(Long id);

    /**
     * 新增标签
     *
     * @param tag 标签信息
     * @return 是否成功
     */
    boolean addTag(Tag tag);

    /**
     * 更新标签
     *
     * @param tag 标签信息
     * @return 是否成功
     */
    boolean updateTag(Tag tag);

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 是否成功
     */
    boolean deleteTag(Long id);
    
    /**
     * 根据文章ID获取标签列表
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> listTagsByArticleId(Long articleId);
    
    /**
     * 保存文章标签关联
     *
     * @param articleId 文章ID
     * @param tagIds 标签ID列表
     * @return 是否成功
     */
    boolean saveArticleTags(Long articleId, List<Long> tagIds);
}